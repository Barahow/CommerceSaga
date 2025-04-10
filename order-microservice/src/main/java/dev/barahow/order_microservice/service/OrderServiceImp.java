package dev.barahow.order_microservice.service;

import dev.barahow.core.dto.Order;
import dev.barahow.core.dto.OrderItem;
import dev.barahow.core.exceptions.InvalidOrderException;
import dev.barahow.core.exceptions.OrderNotFoundException;
import dev.barahow.core.exceptions.OrderProcessingException;
import dev.barahow.core.types.OrderStatus;
import dev.barahow.order_microservice.dao.OrderEntity;
import dev.barahow.order_microservice.mapper.OrderMapper;
import dev.barahow.order_microservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImp implements OrderService{
          // Order failed due to technical issues or other problems
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImp(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order getOrderById(UUID orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id = " + orderId));
        return orderMapper.toDTO(entity);
    }


    @Transactional
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
    @Transactional
    @Override
    public void approveOrder(UUID orderId) {
        OrderEntity orderEntity= orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException("No order is found with id= "+orderId+"in the database"));

        if (orderEntity.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderException("Only orders with status CREATED can be approved/rejected.");
        }
        orderEntity.setStatus(OrderStatus.APPROVED);
        log.info("Approving order: {}", orderId);
        orderRepository.save(orderEntity);



    }
    @Transactional
    @Override
    public void rejectOrder(UUID orderId) {
        OrderEntity orderEntity= orderRepository.findById(orderId).orElseThrow( ()->new OrderNotFoundException("No order is found with id= "+orderId+"in the database"));

        if (orderEntity.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderException("Only orders with status CREATED can be approved/rejected.");
        }
        orderEntity.setStatus(OrderStatus.REJECTED);

        log.info("Rejecting order: {}", orderId);
        orderRepository.save(orderEntity);


    }

    @Transactional
    @Override
    public void cancelOrder(UUID orderId) {
        // users should cancel an order
        OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException("No order with orderId= "+orderId+"found"));

        if(orderEntity.getStatus()!=OrderStatus.CREATED){
            throw new InvalidOrderException("Order needs to be approved to be able to be cancelled");
        }
        orderEntity.setStatus(OrderStatus.CANCELED);
        orderRepository.save(orderEntity);
        log.info("Cancelled order with ID = {}", orderId);
    }

    @Override
    public void orderShipped(UUID orderId) {

    }

    @Override
    public void orderDelivered(UUID orderId) {

    }

    @Override
    public void orderReturned(UUID orderId) {

    }

    @Override
    public void orderFailed(UUID orderId) {

    }

    @Override
    public List<Order> findOrderByCustomer(UUID customerId) {
        List<OrderEntity> orderEntity= orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        if (orderEntity.isEmpty()){
            throw new OrderNotFoundException("Order not found with that customer id: "+customerId);
        }
        return orderEntity.stream().
                map(orderEntity1 -> {
            Order order= new Order();
            BeanUtils.copyProperties(orderEntity1,order);
            return order;
        }).collect(Collectors.toList());
    }
}
