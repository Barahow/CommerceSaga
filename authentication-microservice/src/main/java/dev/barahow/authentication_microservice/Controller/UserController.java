package dev.barahow.authentication_microservice.Controller;

import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.mapper.UserMapper;
import dev.barahow.core.dto.LoginRequestDTO;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.Value;
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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
private final UserMapper userMapper;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @PostMapping("user/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        try {
            Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),loginRequestDTO.getPasswordHash()));

            // fetch the authenticated user
           UserEntity userEntity = (UserEntity) authentication.getPrincipal();

            if (userEntity==null){
                throw new UsernameNotFoundException("User not found ");

            }
            // generate token in repsonse
            // Convert Entity to DTO before returning

            String token = jwtTokenProvider.generateToken(userEntity);

            return ResponseEntity.ok(Collections.singletonMap("token",token));

        }catch (BadCredentialsException exception){
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

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User could not be created");
            //unexpected error
        }
    }


        @PreAuthorize("hasPermission(#userDTO, 'VIEW')")
    @GetMapping("user/{id}")
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

