package dev.barahow.order_microservice.service;

import dev.barahow.core.dto.Order;
import dev.barahow.core.dto.OrderItem;
import dev.barahow.core.exceptions.InvalidOrderException;
import dev.barahow.core.exceptions.OrderProcessingException;
import dev.barahow.core.types.OrderStatus;
import dev.barahow.order_microservice.dao.OrderEntity;
import dev.barahow.order_microservice.mapper.OrderMapper;
import dev.barahow.order_microservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class OrderServiceImp implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImp(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order placeOrder(Order order) {

        validateOrder(order);


      OrderEntity entity= orderMapper.toEntity(order);

      entity.setStatus(OrderStatus.CREATED);

      try {
          orderRepository.save(entity);

      }catch (Exception ex){
          log.error("Error saving order: {}",ex.getMessage());
          throw new OrderProcessingException("failed to place the order ",ex);
      }

      //refetch the entity to ensure auto generated fields like id is populated
        OrderEntity savedentity= orderRepository.findById(entity.getId()).orElseThrow(()->new IllegalArgumentException("Order not found after being saved"));

      // map it back to DTO
        Order createOrder = orderMapper.toDTO(savedentity);


        return createOrder;
    }

    private void validateOrder(Order order) {

        if(order.getCustomerId()==null|| !customerExists(order.getCustomerId())) {
            throw new InvalidOrderException("invalid Customer Id");
        }


        if (order.getItems().isEmpty()){
            throw new InvalidOrderException("Order cannot be placed with no item");
        }

        for(int i =0; i<order.getItems().size();i++) {
            if (!isInStock(order.getItems().get(i))) {
                throw new InvalidOrderException("item "+order.getItems().get(i).getProductName()+ "is out of stock");
            }
        }
    }

    private boolean isInStock(OrderItem orderItem) {

        return orderItem.getQuantity()>0;
    }

    private boolean customerExists(UUID customerId) {

        // if you get to Order-microService we assume the customer is logged in and  the user Id can be extracted
        return true;
    }

    @Override
    public void approveOrder(UUID orderId) {

    }

    @Override
    public void rejectOrder(UUID orderId) {

    }
}
