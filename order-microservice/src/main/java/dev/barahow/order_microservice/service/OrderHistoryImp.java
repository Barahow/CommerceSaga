package dev.barahow.order_microservice.service;

import dev.barahow.core.exceptions.InvalidOrderHistoryException;
import dev.barahow.core.exceptions.OrderHistoryNotFound;
import dev.barahow.core.types.OrderStatus;
import dev.barahow.order_microservice.dao.OrderHistoryEntity;
import dev.barahow.order_microservice.dto.OrderHistory;
import dev.barahow.order_microservice.mapper.OrderMapper;
import dev.barahow.order_microservice.repository.OrderHistoryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderHistoryImp implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderMapper orderMapper;

    public OrderHistoryImp(OrderHistoryRepository orderHistoryRepository, OrderMapper orderMapper) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public void add(UUID orderId, OrderStatus orderStatus) {
        if (orderId == null || orderStatus == null) {
            throw new InvalidOrderHistoryException("Order ID and status cannot be null");
        }

        //for database
        OrderHistoryEntity entity= new OrderHistoryEntity();
        entity.setId(orderId);
        entity.setStatus(orderStatus);
        entity.setCreatedAt(new Timestamp(new Date().getTime()));

      orderHistoryRepository.save(entity);
      log.info("History saved for order: {} with status: {}",orderId,orderStatus);

    }

    @Override
    public List<OrderHistory> findByOrderId(UUID orderId) {
        List<OrderHistoryEntity> orderHistoryEntities= orderHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);

        if(orderHistoryEntities.isEmpty()){
            throw new OrderHistoryNotFound("Order history for this order id not found"+orderId);
        }


        return orderHistoryEntities.stream().map(orderHistoryEntity -> {
            OrderHistory orderHistory= new OrderHistory();
            BeanUtils.copyProperties(orderHistoryEntity,orderHistory);
            return orderHistory;
        }).collect(Collectors.toList());// collectors is easier to read
    }
}
