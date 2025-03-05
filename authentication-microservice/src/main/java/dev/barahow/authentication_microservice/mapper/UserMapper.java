package dev.barahow.authentication_microservice.mapper;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.dto.UserDTO;
import org.springframework.security.core.userdetails.User;


public interface UserMapper {
    UserDTO toDTO(UserEntity userEntity);

    UserEntity toEntity(UserDTO userDTO);
}
