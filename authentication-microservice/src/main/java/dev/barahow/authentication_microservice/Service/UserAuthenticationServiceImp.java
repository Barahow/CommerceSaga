package dev.barahow.authentication_microservice.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.mapper.UserMapper;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.core.dto.LockInfo;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserNotFoundException;
import dev.barahow.core.types.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserAuthenticationServiceImp implements UserAuthenticationService, UserDetailsService {


    public final UserRepository userRepository;
    public final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    public final long MAX_FAILED_ATTEMPTS = 5;

    public final PasswordEncoder passwordEncoder;

    private final long LOCK_TIMEOUT = 10; // 10min

    public UserAuthenticationServiceImp(UserRepository userRepository, UserMapper userMapper, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity= userRepository.findByEmailIgnoreCase(email);
        if (userEntity==null){
            throw new UsernameNotFoundException("user not found with that email");
        }


        // conver userEntity toa  userDetails instance
        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                userEntity.getPasswordHash(),
                userEntity.isEnabled(),
                true,//acount non expired
                true,//credentialsnonExpired
                !userEntity.getLocked().isLocked(),//accountnonLocked
                userEntity.getRole().stream()
                        .map(role -> new SimpleGrantedAuthority("Role_" + role.name()))
                        .collect(Collectors.toSet())

        );



    }
    @Override
    public String login(String email, String password) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);
        if (userEntity==null || !passwordEncoder.matches(userEntity.getPasswordHash(),password)){
            throw new BadCredentialsException("Invalid email or Password");
        }

        return jwtTokenProvider.generateToken(userEntity);
    }
    @Override
    public String getLoggedInUser(String authorizationToken) {
        // u can set your own environment variable
        // using setx MY_APP_SECRET_KEY  "add ur key here"
        String secretKey = System.getenv("MY_APP_SECRET_KEY");

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Secret key is not set in the environment variables");
        }

// Check if the token is valid
        if (authorizationToken == null || !authorizationToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization token format");
        }

        String token = authorizationToken.substring("Bearer ".length());

        //create JWT verification algorithm with our secret key
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());


        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decodedJWT = verifier.verify(token);

        String email = decodedJWT.getSubject();

        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);


        if (userEntity == null) {
            throw new UserNotFoundException("user not found with that email" + email);
        }


        return userMapper.toDTO(userEntity).getEmail();
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);

        if (userEntity == null) {
            throw new UserNotFoundException("User not found with that email " + email);
        }

        return userMapper.toDTO(userEntity);
    }

    @Override
    public UserDTO getUserISLocked(boolean locked) {
        // find all user with their locked status
        List<UserEntity> lockedUsers = userRepository.findByLocked(locked);

        if (lockedUsers.isEmpty()) {
            throw new UserNotFoundException("No User found with that lock status: " + locked);
        }


        // return the first user
        return userMapper.toDTO(lockedUsers.get(0));
    }

    @Override
    public void incrementFailedLoginAttempt(String email, String password) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);

        if (userEntity == null) {
            throw new UserNotFoundException("User not found with that email" + email);
        }


        // if the is locked, check if lock timout has passed
        if (userEntity.getLocked().isLocked()) {

            LocalDateTime lockTime = userEntity.getLocked().getLockTime();
            long minutesSinceLock = ChronoUnit.MINUTES.between(lockTime, LocalDateTime.now());


            if (minutesSinceLock < LOCK_TIMEOUT) {
                throw new IllegalArgumentException("User is locked. Please try again later");
            } else {
                userEntity.getLocked().setLocked(false);
                userEntity.getLocked().setLockTime(null);

            }
        }

        // validate the password
        // if it doesnt match what we got
        // in the database
        if (!passwordEncoder.matches(password, userEntity.getPasswordHash())) {
            int failedAttempts = userEntity.getFailedLoginAttempts() + 1;
            userEntity.setFailedLoginAttempts(failedAttempts);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                // lock the user if it exceeds the max failed attempts
                userEntity.getLocked().setLocked(true);
                userEntity.getLocked().setLockTime(LocalDateTime.now());
                throw new IllegalArgumentException("User has exceeded max login attempts. Account is now locked.");
            }


            // save the updated user state
            userRepository.save(userEntity);

        } else {
            // reset failed login attempts to 0 if the password is correct
            userEntity.setFailedLoginAttempts(0);
            userRepository.save(userEntity);
        }


    }

    @Override
    public void resetUserLock(String email) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);

        if (userEntity == null) {
            throw new UserNotFoundException("user not found with that email" + email);
        }

        // set the user locked back to false
        LockInfo lockInfo = new LockInfo(false, null);
        userEntity.setLocked(lockInfo);

        // save the new state to database
        userRepository.save(userEntity);

    }



    @Override
    public Set<Role> getUserRoles(String loggedInUserEmail) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(loggedInUserEmail);

        if(userEntity==null){
            throw new UserNotFoundException("No user found with that email");
        }


        return userEntity.getRole();

    }


}