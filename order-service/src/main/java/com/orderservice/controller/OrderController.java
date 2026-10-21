package com.orderservice.controller;

import com.orderservice.model.OrderEntity;
import com.orderservice.records.CreateOrderRecord;
import com.orderservice.records.OrderCreatedRecord;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    @Transactional
    public ResponseEntity<OrderEntity> createOrder(@Valid @RequestBody CreateOrderRecord order) {
          OrderEntity orderEntity = new OrderEntity(
            order.productId(),
            order.quantity(),
            order.amount()
          );

          orderRepository.save(orderEntity);

          kafkaTemplate.send("order-created",
            orderEntity.getId().toString(),
            new OrderCreatedRecord(
              UUID.randomUUID().toString(),
              orderEntity.getId().toString(),
              orderEntity.getProductId(),
              orderEntity.getQuantity(),
              orderEntity.getAmount()
            )
          );

          return ResponseEntity.created(URI.create("/orders/" + orderEntity.getId()))
            .body(orderEntity);
    }

    @GetMapping("{id}")
    public OrderEntity findById(@PathVariable UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Order not found: " + id));
    }

    @GetMapping
    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

}

