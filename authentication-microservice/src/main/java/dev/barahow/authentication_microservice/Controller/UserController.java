package dev.barahow.authentication_microservice.Controller;

import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.core.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

   private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasPermission(#userDTO, 'VIEW')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") UUID id) {
        // Retrieve user by id or email and return it
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasPermission(#userDTO, 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDTO) {
        // Update user logic
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @PreAuthorize("hasPermission(#userDTO, 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDTO) {
        // Delete user logic
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}

