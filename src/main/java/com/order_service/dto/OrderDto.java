package com.order_service.dto;

import com.order_service.entity.OrderItem;
import com.order_service.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

}
