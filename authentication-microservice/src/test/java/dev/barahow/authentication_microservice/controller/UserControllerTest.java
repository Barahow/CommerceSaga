package dev.barahow.authentication_microservice.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.config.PasswordEncoderConfig;
import dev.barahow.authentication_microservice.config.SecurityConfig;
import dev.barahow.authentication_microservice.config.TestSecurityConfig;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.filter.CustomPermissionEvaluator;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import dev.barahow.core.types.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j


@WebMvcTest(controllers = UserController.class)
@Import({
        SecurityConfig.class,
        TestSecurityConfig.class

})
@AutoConfigureMockMvc(addFilters = true)

class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private CustomPermissionEvaluator customPermissionEvaluator;
    @MockitoBean
    private UserAuthenticationService userAuthenticationService;

    @MockitoBean
    private UserService userService;

    // Only mock beans that are required for security
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "owner@test.com", // Principal (matches JWT subject)
                null, // Credentials
                List.of(new SimpleGrantedAuthority("CUSTOMER")) // Roles
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    // ---------------------------
    // Test for the login endpoint
    // ---------------------------


   // 1. testLoginSuccess: Simulates a POST to /api/v1/login
   @Test
   void test_loginUserSuccess() throws Exception {
       // Arrange
       final String email = "test@example.com";
       final String password = "validPass123";
       final String expectedToken = "mock-token";

       when(userAuthenticationService.login(email, password))
               .thenReturn(expectedToken);

       // Act & Assert
       mockMvc.perform(post("/api/v1/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value(expectedToken));
   }
    // test login failure

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
    // Test 3: Successful authorized access
    @Test
    void testAuthorizedAccess() throws Exception {
        // 1. Setup test user
        UUID userId = UUID.randomUUID();
        String userEmail = "owner@test.com";
        String password = "securePassword123";

        UserDTO mockUser = new UserDTO();
        mockUser.setId(userId);
        mockUser.setEmail(userEmail);
        mockUser.setPassword(password); // Include password if needed for auth

        // 2. Mock JWT token with ALL required claims
        DecodedJWT mockDecodedJWT = mock(DecodedJWT.class);
        when(mockDecodedJWT.getSubject()).thenReturn(userEmail);
       // when(mockDecodedJWT.getClaim("userId")).thenReturn(Claim.create(userId.toString()));

        Claim rolesClaim = mock(Claim.class);
        when(rolesClaim.asList(String.class)).thenReturn(List.of("CUSTOMER"));
        when(mockDecodedJWT.getClaim("roles")).thenReturn(rolesClaim);

        // 3. Mock token verification
        when(jwtTokenProvider.verifyToken(anyString())).thenReturn(mockDecodedJWT);
        when(jwtTokenProvider.generateToken(any())).thenReturn("mock.token");

        // 4. Mock user service calls
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(userService.getUser(userEmail)).thenReturn(mockUser);

        // 5. Mock successful permission check
        when(customPermissionEvaluator.hasPermission(
                any(Authentication.class),
                eq(userId),
                eq("UserDTO"),
                eq("VIEW")
        )).thenReturn(true);

        // 6. Execute request with proper auth
        mockMvc.perform(get("/api/v1/user/" + userId)
                        .header("Authorization", "Bearer mock.token")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(userEmail));
    }
    @Test
    void test_shouldReturnUser_whenUserRetrievesOwnAccount() throws Exception {
//        // Arrange
//        UUID userId = UUID.randomUUID();
//        String email = "user@gmail.com";
//
//        // Create proper authentication principal
//        Set<Role> roles= new HashSet<>();
//        roles.add(Role.CUSTOMER);
//        CustomUserDetails userDetails = new CustomUserDetails(
//                email,
//                roles
//        );
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(
//                userDetails,
//                null,
//                userDetails.getAuthorities()
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(userId);
//        userDTO.setEmail(email);
//
//        when(userService.getUserById(userId)).thenReturn(userDTO);
//        when(customPermissionEvaluator.hasPermission(
//                any(),
//                eq(userId),
//                eq("UserDTO"),
//                eq("VIEW"))
//        ).thenReturn(true);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/v1/user/" + userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(userId.toString()))
//                .andExpect(jsonPath("$.email").value(email));
    }


    @Test
    void test_shouldReturnUnauthorized_whenUserIsNotLoggedIn() throws Exception {

    }
    @Test
    void test_shouldReturnForbidden_whenUserRetrievesSomeoneElsesAccount() throws Exception {
//        // Arrange
//        UUID userId1 = UUID.randomUUID();
//        String correctEmail = "user1@gmail.com";
//        String wrongEmail = "user2@gmail.com";
//
//        UserDTO requestedUser = new UserDTO();
//        requestedUser.setId(userId1);
//        requestedUser.setEmail(correctEmail);
//
//        UserDTO authenticatedUser = new UserDTO();
//        authenticatedUser.setEmail(wrongEmail);
//
//        // Mock JWT verification
//        String token = "mockToken";
//        Claim rolesClaim = mock(Claim.class);
//        when(rolesClaim.asList(String.class)).thenReturn(List.of(Role.CUSTOMER.name()));
//
//        // Mock UserAuthenticationService
//        when(userAuthenticationService.getLoggedInUser(token)).thenReturn(wrongEmail);
//        when(userAuthenticationService.getUserRoles(wrongEmail)).thenReturn(Set.of(Role.CUSTOMER));
//        when(userAuthenticationService.getUserByEmail(wrongEmail)).thenReturn(authenticatedUser);
//
//        // Mock JWT token
//        DecodedJWT decodedJWT = mock(DecodedJWT.class);
//        when(decodedJWT.getSubject()).thenReturn(wrongEmail);
//        when(decodedJWT.getClaim("roles")).thenReturn(rolesClaim);
//        when(jwtTokenProvider.verifyToken(token)).thenReturn(decodedJWT);
//
//        when(userService.getUserById(userId1)).thenReturn(requestedUser);
//
//        // Mock permission evaluator to deny access
//        when(customPermissionEvaluator.hasPermission(
//                any(),
//                eq(userId1),
//                eq("UserDTO"),
//                eq("VIEW")
//        )).thenReturn(false);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/v1/user/" + userId1)
//                        .header("Authorization", "Bearer " + token)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
    }
    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}