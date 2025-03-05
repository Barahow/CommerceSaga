package dev.barahow.authentication_microservice.mapper;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.dto.UserDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImp  implements UserMapper {
    @Override
    public UserDTO toDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        return new UserDTO.Builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .passwordHash(userEntity.getPasswordHash())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .address(userEntity.getAddress())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .role(userEntity.getRole())
                .enabled(userEntity.isEnabled())
                .locked(userEntity.isLocked())
                .build();
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        return UserEntity.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .passwordHash(userDTO.getPasswordHash())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .address(userDTO.getAddress())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .role(userDTO.getRole())
                .enabled(userDTO.isEnabled())
                .locked(userDTO.isLocked())
                .build();
    }
    }


