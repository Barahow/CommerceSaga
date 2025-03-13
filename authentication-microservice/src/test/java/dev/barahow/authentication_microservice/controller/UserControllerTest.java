package dev.barahow.authentication_microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.config.SecurityConfig;
import dev.barahow.authentication_microservice.filter.CustomAuthorizationFilter;
import dev.barahow.authentication_microservice.filter.CustomPermissionEvaluator;
import dev.barahow.authentication_microservice.security.CustomAccessDeniedHandler;
import dev.barahow.core.dto.LoginRequestDTO;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import dev.barahow.core.types.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(value = UserController.class,
        excludeAutoConfiguration = {SpringDataWebAutoConfiguration.class
, SecurityFilterAutoConfiguration.class}
)


@AutoConfigureMockMvc

@Import(SecurityConfig.class)// load security config and CustomPermission class
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private  UserService userService;

    @MockitoBean
    private UserAuthenticationService userAuthenticationService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    // Mock other SecurityConfig dependencies

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private PasswordEncoder passwordEncoder;


    @MockitoBean
    private AuthenticationConfiguration authenticationConfiguration;

    @MockitoBean
    private CustomPermissionEvaluator customPermissionEvaluator;
    @MockitoBean
    private CustomAuthorizationFilter customAuthorizationFilter;



    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // ---------------------------
    // Test for the login endpoint
    // ---------------------------


   // 1. testLoginSuccess: Simulates a POST to /api/v1/login
    @Test
    void test_loginUserSuccess() throws Exception {
        //arrange
        String email = "user@gmail.com";
        String password="password";
        LoginRequestDTO loginRequestDTO= new LoginRequestDTO();
        String role = "CUSTOMER";
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);


        String token= "dummyToken";

        when(userAuthenticationService.login(email,password)).thenReturn(token);
        //act
        MvcResult result= mockMvc.perform(post("/api/v1/login")
                        .with(csrf())
                        .with(user(email).password(password).roles(Role.CUSTOMER.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andReturn();


        //assert
        String responseContent = result.getResponse().getContentAsString();

        assertTrue(responseContent.contains(token),"Response should contain the token ");

    }

    // test login failure
    @Test
    public void testloginFailure() throws Exception {
       // arrange
        String email = "user@email.com";
        String password= "wrongpassword";
        LoginRequestDTO loginRequestDTO= new LoginRequestDTO();
        loginRequestDTO.setEmail(email);
        loginRequestDTO.setPassword(password);


        when(userAuthenticationService.login(email,password)).thenThrow(new BadCredentialsException("invalid credentials"));
        //act

        MvcResult result = mockMvc.perform(post("/api/v1/login")
                        .with(csrf())
                        .with(user(email).password(password).roles(Role.CUSTOMER.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andReturn();


        //assert

        String responseContent = result.getResponse().getContentAsString();
        assertEquals("Invalid email or password ",responseContent,"error message must match");
    }

    @Test
    void registration() throws Exception {

      /*  @PostMapping("/registration")
        public ResponseEntity<?> registration(@RequestBody @Valid UserDTO userDTO) {
            try {
                UserDTO createdUser = userService.createUser(userDTO);


                return ResponseEntity.ok(Collections.singletonMap("user", createdUser));

            } catch (UserAlreadyExistsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
            }catch (Exception ex) {
                log.error(ex
                        .getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User could not be created");

                //unexpected error
            }*/

        //arrange

        String email= "abdul@gmail.com";
        String password="1234";
        String firstName = "john";
        String lastName= "Doe";
        String address="123 Main St";
         UserDTO userDTO = new UserDTO();
         userDTO.setEmail(email);
         userDTO.setPassword(password);
         userDTO.setFirstName(firstName);
         userDTO.setLastName(lastName);
         userDTO.setAddress(address);

         when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

         // Act
        MvcResult result = mockMvc.perform(post("/api/v1/registration")
                .with(csrf())
                        .with(user(email).password(password).roles(Role.CUSTOMER.name()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isOk())
                .andReturn();

        //Assert explicitly
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains(asJsonString(userDTO)),"Response should contain the return user as json");


    }

    @Test
    void test_registrationConflict() throws Exception {
        //Arrange
        String email= "abdul@gmail.com";
        String password="1234";
        String firstName = "john";
        String lastName= "Doe";
        String address="123 Main St";
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setAddress(address);

        when(userService.createUser(any(UserDTO.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

        // act

        MvcResult mvcResult=mockMvc.perform(post("/api/v1/registration")
                        .with(csrf())
                        .with(user(email).password(password).roles(Role.CUSTOMER.name()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isConflict())
                .andReturn();



        //assert
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains("User already exists"),"Error message must match");

    }


    @Test
    void test_shouldReturnUser_whenUserRetrievesOwnAccount() throws Exception {

      /*  // Retrieve user by id or email and return it
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);*/
        //Arrange

        String email = "user@gmail.com";
        String password= "1234";
        UUID userId = UUID.randomUUID();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setPassword(password);
        userDTO.setEmail(email);

        when(userService.getUserById(userId)).thenReturn(userDTO);
        //Act

        MvcResult mvcResult= mockMvc.perform(get("/api/v1/user/" + userId.toString())
                        .with(csrf())
                .with(user(email).password(password).roles(Role.CUSTOMER.name()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andReturn();

        //Assert
        String responseContent = mvcResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(userDTO.getEmail()),"Response should contain user Email");

    }

    @Test
    void test_shouldReturnUnauthorized_whenUserIsNotLoggedIn() throws Exception {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId1);
        userDTO.setEmail("user1@gmail.com");

        when(userService.getUserById(userId1)).thenReturn(userDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/" + userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("")); // Default Spring Security message (empty)
    }


    @Test
    void test_shouldReturnForbidden_whenUserRetrievesSomeoneElsesAccount() throws Exception {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId1);
        userDTO.setEmail("user1@gmail.com");

        when(userService.getUserById(userId1)).thenReturn(userDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/user/" + userId1)
                        .with(csrf())
                        .with(user("user2@gmail.com").password("1234").roles(Role.CUSTOMER.name()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()) // Expect 403
                .andExpect(content().json("{\"error\": \"You are not authorized to access this resource.\"}"));
    }
    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}