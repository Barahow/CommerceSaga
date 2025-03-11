package dev.barahow.authentication_microservice.Controller;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.mapper.UserMapper;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.dto.LoginRequestDTO;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {

   private final UserService userService;
   private final UserAuthenticationService userAuthenticationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
private final UserMapper userMapper;

    public UserController(UserService userService, UserAuthenticationService userAuthenticationService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userService = userService;
        this.userAuthenticationService = userAuthenticationService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        try {

log.info("email {}",loginRequestDTO.getEmail());
log.info("password {}",loginRequestDTO.getPassword());
          String token=  userAuthenticationService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

            // generate token in repsonse
            // Convert Entity to DTO before returning



            return ResponseEntity.ok(Collections.singletonMap("token",token));

        }catch (BadCredentialsException exception){
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password ");
        }

    }

    @PostMapping("/registration")
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
        }
    }


        @PreAuthorize("hasPermission(#userDTO, 'VIEW')")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") UUID id) {
        // Retrieve user by id or email and return it
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user").toUriString());

        UserDTO createdDTo = userService.createUser(userDTO);
        log.info("User created {}",createdDTo.toString());



        return ResponseEntity.created(uri).body(createdDTo);

    }

    @PreAuthorize("hasPermission(#userDTO, 'UPDATE')")
    @PutMapping("user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDTO) {
        // Update user logic
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @PreAuthorize("hasPermission(#userDTO, 'DELETE')")
    @DeleteMapping("user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDTO) {
        // Delete user logic
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

