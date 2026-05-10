package com.order_service.service.impl;

import com.order_service.dto.OrderDto;
import com.order_service.dto.OrderItemDto;
import com.order_service.dto.response.ProductResponse;
import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import com.order_service.entity.OrderStatus;
import com.order_service.repository.OrderRepository;
import com.order_service.service.OrderService;
import com.order_service.service.helper.ProductServiceHelper;
import com.order_service.kafka.event.OrderPlacedEvent;
import com.order_service.kafka.producer.OrderProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private ModelMapper modelMapper;
    private final ProductServiceHelper productServiceHelper;
    private final OrderProducer orderProducer;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {

        Order order = new Order();
        order.setUserId(orderDto.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        double totalAmount = 0;

        for (OrderItemDto dto : orderDto.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(dto.getProductId());
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setPrice(dto.getPrice());

            orderItem.setOrder(order);

            totalAmount += dto.getPrice() * dto.getQuantity();

            items.add(orderItem);
        }
        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {} for user: {}", savedOrder.getId(), orderDto.getUserId());

        // Send Kafka Event
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderId(savedOrder.getId());
        event.setUserEmail(orderDto.getUserEmail());
        event.setAmount(savedOrder.getTotalAmount());
        log.info("Sending order placed event to Kafka for order ID: {}", savedOrder.getId());
        orderProducer.sendMessage(event);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(
                order -> {
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);

                    for (OrderItemDto orderItemDto : orderDto.getItems()) {

                        log.debug("Calling Product-Service for product ID: {}", orderItemDto.getProductId());

                        ProductResponse productResponse = productServiceHelper
                                .fetchProduct(orderItemDto.getProductId());

                        orderItemDto.setProductName(productResponse.getName());
                        orderItemDto.setPrice(productResponse.getPrice());

                    }
                    return orderDto;
                }).toList();
    }
}