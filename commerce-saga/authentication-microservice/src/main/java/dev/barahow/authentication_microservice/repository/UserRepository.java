package dev.barahow.authentication_microservice.repository;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.types.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmailIgnoreCase(String email);

    Role findRoleByName(String customer);
}
