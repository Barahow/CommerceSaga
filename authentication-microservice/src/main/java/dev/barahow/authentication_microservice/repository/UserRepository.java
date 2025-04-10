package dev.barahow.authentication_microservice.repository;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.types.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    UserEntity findByEmailIgnoreCase(String email);


    List<UserEntity> findByLocked(boolean locked);


}
