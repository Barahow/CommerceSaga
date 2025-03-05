package dev.barahow.core.dto;

import dev.barahow.core.types.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {

    private UUID id;

    private String email;


    private String passwordHash;


    private String firstName;


    private String lastName;

    private String address;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private Role role;

    private boolean enabled;


    private boolean locked;

}
