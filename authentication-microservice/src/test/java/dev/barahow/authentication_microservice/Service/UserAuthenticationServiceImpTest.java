package dev.barahow.authentication_microservice.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.dao.LockInfoEntity;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.mapper.UserMapper;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.dto.LockInfo;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.types.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.xml.crypto.Data;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// it will ensures that each tests will reload in a fresh context
@DirtiesContext
@ExtendWith(MockitoExtension.class)

class UserAuthenticationServiceImpTest {

    @Mock
    public  UserRepository userRepository;
    // In your test class

    @Mock
    private JwtTokenProvider jwtTokenProvider;

     @Mock
    public PasswordEncoder passwordEncoder;

     @Mock
     public UserMapper userMapper;

    // Explicitly provide a test secret key
    private final String testSecretKey = "test-secret-key";

    @InjectMocks
    private UserAuthenticationServiceImp userAuthenticationService ;

    @BeforeEach
   void setup() {
        userAuthenticationService= new UserAuthenticationServiceImp(
                userRepository,userMapper,jwtTokenProvider,passwordEncoder,testSecretKey
        );
    }
    // naming convention
    // test<System Under Test>_condition or state change_expectedResult
    @Test
    void testloginUser_whenGivenAValidEmailAndPassword() {
        //arrange
        String email = "test@gmail.com";
        String rawPassword= "1234";

        String encodedPassword = "$2a$10$J959J4aq99MzZu9cYiyJWOjyA2Wa3G/DCbKW3Uva/K0j9afNSuRzm"; // Fake bcrypt hash

        UserEntity userEntity= new UserEntity();

        userEntity.setEmail(email);
        userEntity.setPassword(encodedPassword);
        userEntity.setId(UUID.randomUUID());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);
        userEntity.setRole(roles);

        LockInfoEntity lockInfo = new LockInfoEntity();
        lockInfo.setLockTime(null);
        lockInfo.setLocked(false);

        userEntity.setLocked(lockInfo);




        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(userEntity);

        lenient().when(passwordEncoder.matches(rawPassword,encodedPassword)).thenReturn(true);


        String expectedToken ="mockedJWTToken";
        // Use lenient() to avoid unnecessary stubbing error                       // use any()
         lenient().when(jwtTokenProvider.generateToken(any(CustomUserDetails.class))).thenReturn(expectedToken);


        //act
        String token = userAuthenticationService.login(email,rawPassword);

        //assert

        assertEquals(expectedToken,token);

    }

    @Test
    void testgetLoggedInUser_whenGivenAValidJWTToken() {
        //arrange



        String email = "test@gmail.com";

        // mock user entity
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        //mock userDto
        UserDTO userDto = new UserDTO();
        userDto.setEmail(email);


        // set up a vlid jwt Token
        Algorithm algorithm = Algorithm.HMAC256(testSecretKey.getBytes());
        String token = JWT.create()
                .withSubject(email)
                .sign(algorithm);

        String authorizationToken = "Bearer "+token;

        //Mock repository behavior
        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(userEntity);

        //mock mapper behavior
        when(userMapper.toDTO(userEntity)).thenReturn(userDto);

        //act
        String returnedEmail = userAuthenticationService.getLoggedInUser(authorizationToken);

        //assert

        assertEquals(email,returnedEmail);
    }
}