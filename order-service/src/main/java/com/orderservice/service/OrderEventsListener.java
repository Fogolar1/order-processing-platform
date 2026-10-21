package com.orderservice.service;

import com.orderservice.model.OrderEntity;
import com.orderservice.records.CreateOrderRecord;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventsListener {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "inventory-reserved", groupId = "order-service")
    @Transactional
    public void inventoryReserved(Map<String, Object> message) {
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(String.valueOf(message.get("orderId")))).orElseThrow();

        if(!orderEntity.getStatus().equals("PENDING_INVENTORY")) return;

        orderEntity.setStatus("PENDING_PAYMENT");
        orderRepository.save(orderEntity);

      kafkaTemplate.send("payment-requested",
        orderEntity.getId().toString(),
        Map.of(
          "eventId", UUID.randomUUID().toString(),
          "orderId", orderEntity.getId().toString(),
          "amount", orderEntity.getAmount()
        ));
    }

    @KafkaListener(topics = "inventory-rejected", groupId = "order-service")
    @Transactional
    public void inventoryRejected(Map<String, Object> message) {
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(String.valueOf(message.get("orderId")))).orElseThrow();
        if(!orderEntity.getStatus().equals("PENDING_INVENTORY")) return;

        orderEntity.setStatus("REJECTED_INVENTORY");
        orderRepository.save(orderEntity);
    }

    @KafkaListener(topics = "payment-approved", groupId = "order-service")
    @Transactional
    public void paymentApproved(Map<String, Object> message) {
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(String.valueOf(message.get("orderId")))).orElseThrow();
        if(!orderEntity.getStatus().equals("PENDING_PAYMENT")) return;

        orderEntity.setStatus("CONFIRMED");
        orderRepository.save(orderEntity);

        kafkaTemplate.send("order-confirmed",
          orderEntity.getId().toString(),
          Map.of(
            "eventId", UUID.randomUUID().toString(),
            "orderId", orderEntity.getId().toString()
          ));
    }

    @KafkaListener(topics = "payment_rejected", groupId = "order-service")
    @Transactional
    public void paymentRejected(Map<String, Object> message) {
        OrderEntity orderEntity = orderRepository.findById(UUID.fromString(String.valueOf(message.get("orderId")))).orElseThrow();
        if(!orderEntity.getStatus().equals("PENDING_PAYMENT")) return;

        orderEntity.setStatus("REJECTED_PAYMENT");
        orderRepository.save(orderEntity);

        kafkaTemplate.send("inventory-release-requested",
          orderEntity.getId().toString(),
          Map.of(
            "eventId", UUID.randomUUID().toString(),
            "orderId", orderEntity.getId().toString()
          ));
    }
}
