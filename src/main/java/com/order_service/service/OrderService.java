package com.order_service.service;

import com.order_service.dto.OrderDto;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);

    List<OrderDto> getOrderHistory(Long userId);

}
