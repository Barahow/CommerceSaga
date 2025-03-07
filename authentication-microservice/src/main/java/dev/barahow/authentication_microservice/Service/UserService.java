package dev.barahow.authentication_microservice.Service;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDTO saveUser(UserDTO user);

    void addRoleToUser(String email);

    UserDTO createUser(UserDTO user);
    UserDTO getUser(String email);
    Page<UserDTO> getUsers(Pageable pageable);

    void updateUser(UUID id, UserDTO userDTO);

    UserDTO getUserById(UUID id);

    void deleteUser(UUID id);
}
