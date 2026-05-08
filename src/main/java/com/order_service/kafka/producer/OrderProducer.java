package com.order_service.kafka.producer;

import com.order_service.kafka.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    public static final String TOPIC = "order-topics";

    public void sendMessage(OrderPlacedEvent event) {
        log.info("Sending OrderPlacedEvent to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
