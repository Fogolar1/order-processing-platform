package com.paymentservice.service;

import com.paymentservice.model.PaymentEntity;
import com.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventsListener {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-requested", groupId = "payment-service")
    @Transactional
    public void processPayment(Map<String, Object> message) {
        String orderId =  String.valueOf(message.get("orderId"));

        if(paymentRepository.findByOrderId(orderId).isPresent()) return;

        BigDecimal amount = new BigDecimal(String.valueOf(message.get("amount")));

        boolean approved = amount.compareTo(new BigDecimal("10000.00")) <= 0;

        String status = approved ? "approved" : "rejected";

        paymentRepository.save(new PaymentEntity(orderId, amount, status));

        Map<String, Object> headers = Map.of(
          "eventId", UUID.randomUUID().toString(),
          "orderId", orderId,
          "reason", approved ? "APPROVED" : "SIMULATED_DECLINE_AMOUNT_LIMIT"
        );

        kafkaTemplate.send(approved ? "payment-approved" : "payment-rejected", orderId, headers);
    }
}
