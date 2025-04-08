package dev.barahow.order_microservice.controller;

import dev.barahow.order_microservice.dto.CreateOrderRequest;
import dev.barahow.order_microservice.dto.OrderHistoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {


    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest){

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Order created successfully");


    }




  /*  @GetMapping("/orders/history/{id}")

    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable("id") UUID orderId) {

        return ResponseEntity.ok();
    }
    */
    }

