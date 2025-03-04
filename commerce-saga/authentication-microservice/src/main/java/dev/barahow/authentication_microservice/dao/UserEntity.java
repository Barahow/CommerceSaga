package dev.barahow.authentication_microservice.dao;

import dev.barahow.core.types.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
        private UUID id;

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email should be valid")
        @Column(name = "user_email", nullable = false, unique = true)
        private String email;

        @NotBlank(message = "Password is mandatory")
        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        @Column(nullable = false, name = "first_name")
        private String firstName;

        @Column(nullable = false, name = "last_name")
        private String lastName;

        @Column(name = "address")
        private String address;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(nullable = false)
        private LocalDateTime updatedAt;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        // To handle account activation or deactivation
        @Column(nullable = false)
        private boolean enabled;

        @Column(nullable = false)
        private boolean locked;


}
