package dev.barahow.authentication_microservice.mapper;

import dev.barahow.authentication_microservice.dao.LockInfoEntity;
import dev.barahow.core.dto.LockInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LockInfoMapper {

public LockInfo toDto(boolean locked, LocalDateTime lockTime){
    return new LockInfo(locked,lockTime);
}

public LockInfoEntity toEntity(boolean locked, LocalDateTime localDateTime){
    return new LockInfoEntity(locked,localDateTime);
}
}
