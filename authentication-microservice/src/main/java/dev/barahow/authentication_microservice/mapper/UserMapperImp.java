package dev.barahow.authentication_microservice.mapper;

import dev.barahow.authentication_microservice.dao.LockInfoEntity;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.dto.LockInfo;
import dev.barahow.core.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImp  implements UserMapper {

  private final LockInfoMapper lockInfoMapper;

    public UserMapperImp(LockInfoMapper lockInfoMapper) {
        this.lockInfoMapper = lockInfoMapper;
    }

    @Override
    public UserDTO toDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        LockInfo lockInfo= lockInfoMapper.toDto(userEntity.getLocked().isLocked(),userEntity.getLocked().getLockTime());

        return new UserDTO.Builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .passwordHash(userEntity.getPassword())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .address(userEntity.getAddress())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .role(userEntity.getRole())
                .enabled(userEntity.isEnabled())
                .locked(lockInfo)
                .build();
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
      LockInfoEntity lock= lockInfoMapper.toEntity(userDTO.isLocked().isLocked(),userDTO.isLocked().getLockTime());

        return UserEntity.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .address(userDTO.getAddress())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .role(userDTO.getRole())
                .enabled(userDTO.isEnabled())
                .locked(lock)
                .build();
    }
    }


