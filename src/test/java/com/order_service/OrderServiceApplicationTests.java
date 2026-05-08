package com.order_service;

import com.order_service.kafka.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class OrderServiceApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

	@Autowired
	private com.order_service.kafka.producer.OrderProducer orderProducer;

	@Test
	void testSendMessage() {
		OrderPlacedEvent event = new OrderPlacedEvent();
		event.setOrderId(1L);
		event.setUserEmail("test@example.com");
		event.setAmount(100.0);

		orderProducer.sendMessage(event);

		org.mockito.Mockito.verify(kafkaTemplate).send(
				org.mockito.ArgumentMatchers.eq(com.order_service.kafka.producer.OrderProducer.TOPIC),
				org.mockito.ArgumentMatchers.eq(event)
		);
	}

}
