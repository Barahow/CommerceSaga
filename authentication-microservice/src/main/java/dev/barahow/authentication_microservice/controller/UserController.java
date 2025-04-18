package dev.barahow.authentication_microservice.controller;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.config.PasswordEncoderConfig;
import dev.barahow.core.dto.LoginRequestDTO;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {

   private final UserService userService;
   private final UserAuthenticationService userAuthenticationService;




    public UserController(UserService userService, UserAuthenticationService userAuthenticationService) {
        this.userService = userService;
        this.userAuthenticationService = userAuthenticationService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){



          String token=  userAuthenticationService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());

            // generate token in repsonse
            // Convert Entity to DTO before returning

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isMatch = encoder.matches("1234", "$2a$10$VbgjdzT2Hwh6oJoBkDgbD.TuLC9U0.PZo/2bpMETWyRXX5W9wwSfK");

            log.info("matches{}", isMatch);
            return ResponseEntity.ok(Collections.singletonMap("token",token));



    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserDTO userDTO) {


            UserDTO createdUser = userService.createUser(userDTO);


            return ResponseEntity.ok(Collections.singletonMap("user", createdUser));





    }



    @GetMapping("/user/{id}")
    @PreAuthorize("hasPermission(#id, 'UserDTO', 'VIEW')")

    public ResponseEntity<UserDTO> getUser(@PathVariable("id") UUID id) {
        // Retrieve user by id or email and return it
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }



    @PreAuthorize("hasPermission(#id, 'UserDTO', UPDATE')")
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

