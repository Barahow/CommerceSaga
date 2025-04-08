package dev.barahow.order_microservice.mapper;

import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.core.dto.Order;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.order_microservice.dao.OrderEntity;


public interface OrderMapper {
    Order toDTO(OrderEntity orderEntity);

    OrderEntity toEntity(Order orderDTO);
}
