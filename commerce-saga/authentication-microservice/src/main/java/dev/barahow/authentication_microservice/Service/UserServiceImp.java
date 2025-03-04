package dev.barahow.authentication_microservice.Service;

import dev.barahow.authentication_microservice.config.PasswordEncoderConfig;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.core.types.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        if(user== null){
            throw new IllegalArgumentException("User cannot be null");
        }

        log.error("Saving new user {} to the database",user.getEmail());;

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        user.setLocked(false);
        return userRepository.save(user);
    }

    @Override
    public UserEntity saveRole(UserEntity userRole) {

       if (userRole==null){
           throw new IllegalArgumentException("Role cannot be null");
       }

       log.info("Saving new Role {} to the database",userRole);

       return userRepository.save(userRole);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {

    }

    @Override
    public UserEntity createUser(UserEntity user) {

        hashPassword(user);
        UserEntity newUser  = userRepository.findByEmailIgnoreCase(user.getEmail());
        if(newUser!= null){
            throw new IllegalStateException("email already exist");
        }else {
            Optional<Role> userRole = Optional.ofNullable(userRepository.findRoleByName("CUSTOMER"));


            userRole.ifPresent(role -> {
                // Do something with the role if it exists
                System.out.println("Role found: " + role.name());
            });

            userRole.orElseThrow(() -> new RuntimeException("default user Role not found"));

            user.setRole(userRole.get());
            user.setLocked(false);
     newUser = UserEntity.builder()
                    .email(user.getEmail())
                    .passwordHash(user.getPasswordHash())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .enabled(true)
                    .locked(false)
             .build();


        }




log.info("Created a new user {}",newUser);


        return userRepository.save(newUser);

    }

    @Override
    public UserEntity getUser(String email) {
        return null;
    }

    @Override
    public Page<UserEntity> getUsers(Pageable pageable) {
        return null;
    }


    private void hashPassword(UserEntity user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

    }
}
