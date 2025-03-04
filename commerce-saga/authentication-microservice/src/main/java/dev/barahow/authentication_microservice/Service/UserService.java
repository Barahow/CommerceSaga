package dev.barahow.authentication_microservice.Service;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.repository.UserRepository;
import dev.barahow.core.types.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserEntity saveUser(UserEntity user);
    UserEntity saveRole(UserEntity UserEntity);

    void addRoleToUser(String email, String roleName);

    UserEntity createUser(UserEntity user);
    UserEntity getUser(String email);
    Page<UserEntity> getUsers(Pageable pageable);
}
