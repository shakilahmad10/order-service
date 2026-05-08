package com.order_service.service.helper;

import com.order_service.dto.response.ProductResponse;
import com.order_service.service.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceHelper {

    private ProductClient productClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public ProductResponse fetchProduct(Long id) {
        return productClient.getProductById(id);
    }

    public ProductResponse productFallback(Long id, Exception ex) {
        System.out.println("Circuit breaker fallback triggered");

        ProductResponse fallback = new ProductResponse();
        fallback.setId(id);
        fallback.setName("Unavailable");
        fallback.setPrice(0.0);

        return fallback;
    }
}