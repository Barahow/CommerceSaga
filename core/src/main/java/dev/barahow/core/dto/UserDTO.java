package dev.barahow.core.dto;

import dev.barahow.core.types.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;



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


    private LockInfo locked;

    public UserDTO() {
    }


    public UserDTO(UUID id, String email, String passwordHash, String firstName, String lastName, String address, LocalDateTime createdAt, LocalDateTime updatedAt, Role role, boolean enabled, LockInfo locked) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
        this.enabled = enabled;
        this.locked = locked;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LockInfo isLocked() {
        return locked;
    }

    public void setLocked(LockInfo locked) {
        this.locked = locked;
    }

    // Builder class
    public static class Builder {
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
        private LockInfo locked;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder locked(LockInfo locked) {
            this.locked = locked;
            return this;
        }

        // Build the final UserDTO object
        public UserDTO build() {
            return new UserDTO(id, email, passwordHash, firstName, lastName, address, createdAt, updatedAt, role, enabled, locked);
        }
    }
}

