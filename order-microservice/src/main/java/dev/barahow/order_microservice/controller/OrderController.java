package dev.barahow.order_microservice.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import dev.barahow.core.dto.Order;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.exceptions.AuthenticationException;
import dev.barahow.core.exceptions.UserNotFoundException;
import dev.barahow.core.exceptions.error.ErrorResponse;
import dev.barahow.order_microservice.component.JwtUtils;
import dev.barahow.order_microservice.dto.CreateOrderRequest;
import dev.barahow.order_microservice.dto.CreateOrderResponse;
import dev.barahow.order_microservice.dto.OrderHistoryResponse;
import dev.barahow.order_microservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Log4j2
public class OrderController {


    private final OrderService orderService;
    private final JwtUtils jwtUtils;

    public OrderController(OrderService orderService, JwtUtils jwtUtils) {
        this.orderService = orderService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    @PreAuthorize("hasPermission(@jwtUtils.extractCustomerIdFromJWT(),'UserDTO','VIEW'")
    public ResponseEntity<List<Order>> getOrders() {
        UUID customerId= jwtUtils.extractCustomerIdFromJWT();
        List<Order> orders= orderService.findOrderByCustomer(customerId);

        return ResponseEntity.ok(orders);
    }
    @PostMapping
    @PreAuthorize("hasPermission(@jwtUtils.extractCustomerIdFromJWT(), 'UserDTO', 'POST')")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest){


        //get the customerId from JwtUtils
      UUID customerId = jwtUtils.extractCustomerIdFromJWT();

        Order order= new Order();

        BeanUtils.copyProperties(order,createOrderRequest);
          //pass the order to the service
        order.setCustomerId(customerId);


        Order createdOrder = orderService.placeOrder(order);

        CreateOrderResponse createOrderResponse= new CreateOrderResponse();
        BeanUtils.copyProperties(createdOrder,createOrderResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(createOrderResponse);


    }




  /*  @GetMapping("/orders/history/{id}")

    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable("id") UUID orderId) {

        return ResponseEntity.ok();
    }
    */
    }

