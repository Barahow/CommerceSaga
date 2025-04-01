package dev.barahow.authentication_microservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.filter.CustomPermissionEvaluator;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import dev.barahow.core.types.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
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




@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")

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
      // arrange
       final String email = "test@example.com";
       final String password = "validPass123";
       final String expectedToken = "mock-token";

       Mockito.when(userAuthenticationService.login(email, password))
               .thenReturn(expectedToken);

         // assert
       mockMvc.perform(post("/api/v1/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value(expectedToken));
   }
    // test login failure

    @Test
    void test_registrationSuccess() throws Exception {



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

         Mockito.when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

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

        Mockito.when(userService.createUser(any(UserDTO.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

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
        // Arrange
        UUID userId1 = UUID.randomUUID();
        String ownerEmail = "owner@test.com";

        UserDTO requestUser = new UserDTO();
        requestUser.setId(userId1);
        requestUser.setEmail(ownerEmail);


        //generate a vlid JWT Token for the woner
        String validToken = JWT.create()
                .withSubject(ownerEmail)
                .withClaim("roles",List.of("CUSTOMER"))
                .sign(Algorithm.HMAC256("irrelevant"));

        //act
        Mockito.when(userService.getUserById(userId1)).thenReturn(requestUser);
        Mockito.when(customPermissionEvaluator.hasPermission(
                Mockito.any(Authentication.class),
                Mockito.eq(userId1),
                Mockito.eq("UserDTO"),
                Mockito.eq("VIEW")
        )).thenReturn(true);


        // stub the JWT token verification so the filter can extrct subjecnt and claims
        DecodedJWT mockDecodedJWT= Mockito.mock(DecodedJWT.class);
        Mockito.when(mockDecodedJWT.getSubject()).thenReturn(ownerEmail);
        Claim rolesClaim = Mockito.mock(Claim.class);
        Mockito.when(rolesClaim.asList(String.class)).thenReturn(List.of("CUSTOMER"));

        Mockito.when(mockDecodedJWT.getClaim("roles")).thenReturn(rolesClaim);

        Mockito.when(jwtTokenProvider.verifyToken(anyString())).thenReturn(mockDecodedJWT);



        mockMvc.perform(get("/api/v1/user/"+userId1)
                        .with(csrf())
                        .header("Authorization","Bearer " + validToken))
                .andExpect(status().isOk());
    }

//--------------------------------
// Test for unauthorized access when no user is logged in
//------------------------------------
    @Test
    void test_shouldReturnUnauthorized_whenUserIsNotLoggedIn() throws Exception {
SecurityContextHolder.clearContext();
mockMvc.perform(get("/api/v1/user/" + UUID.randomUUID())
        .with(csrf()))
        .andExpect(status().isUnauthorized());
    }


    @Test
    void test_shouldReturnForbidden_whenUserRetrievesSomeoneElsesAccount() throws Exception {


        // Arrange
        UUID userId1 = UUID.randomUUID();
       String ownerEmail = "owner@test.com";
       String anotherEmail = "another@test.com";
       UserDTO requestUser = new UserDTO();
       requestUser.setId(userId1);
       requestUser.setEmail(anotherEmail);


       //generate a vlid JWT Token for the woner
        String validToken = JWT.create()
                .withSubject(ownerEmail)
                        .withClaim("roles",List.of("CUSTOMER"))
                                .sign(Algorithm.HMAC256("irrelevant"));

       //act
        Mockito.when(userService.getUserById(userId1)).thenReturn(requestUser);
        Mockito.when(customPermissionEvaluator.hasPermission(
                Mockito.any(Authentication.class),
                Mockito.eq(userId1),
                Mockito.eq("UserDTO"),
                Mockito.eq("VIEW")
        )).thenReturn(false);


        // stub the JWT token verification so the filter can extrct subjecnt and claims
        DecodedJWT mockDecodedJWT= Mockito.mock(DecodedJWT.class);
        Mockito.when(mockDecodedJWT.getSubject()).thenReturn(ownerEmail);
        Claim rolesClaim = Mockito.mock(Claim.class);
        Mockito.when(rolesClaim.asList(String.class)).thenReturn(List.of("CUSTOMER"));

        Mockito.when(mockDecodedJWT.getClaim("roles")).thenReturn(rolesClaim);

        Mockito.when(jwtTokenProvider.verifyToken(anyString())).thenReturn(mockDecodedJWT);



        mockMvc.perform(get("/api/v1/user/"+userId1)
                .with(csrf())
                        .header("Authorization","Bearer " + validToken))
                .andExpect(status().isForbidden());

    }
    @Test
    void updateUser() {
        // TODO: Implement test for update user functionality
    }

    @Test
    void deleteUser() {
        // TODO: Implement test for delete user functionality
    }
}