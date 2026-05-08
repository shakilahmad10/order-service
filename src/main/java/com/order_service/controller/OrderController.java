package com.order_service.controller;

import com.order_service.dto.OrderDto;
import com.order_service.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto){
        OrderDto response = orderService.createOrder(orderDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable("id") Long userId){
        List<OrderDto> response = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(response);
    }
}
