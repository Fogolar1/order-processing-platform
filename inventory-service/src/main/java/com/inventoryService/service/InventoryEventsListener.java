package com.inventoryService.service;

import com.inventoryService.model.StockEntity;
import com.inventoryService.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryEventsListener {

    private final StockRepository stockRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-created", groupId = "inventory-service")
    @Transactional
    public void orderCreated(Map<String, Object> message){
        String orderId = String.valueOf(message.get("orderId"));
        String productId = String.valueOf(message.get("productId"));
        int quantity = Integer.parseInt(message.get("quantity").toString());

        Optional<StockEntity> stock = stockRepository.lockById(orderId);

        if(stock.isPresent() && stock.get().getAvailable() >= quantity) {
            StockEntity stockEntity = stock.get();
            stockEntity.setAvailable(stockEntity.getAvailable() - quantity);
            stockRepository.save(stockEntity);

            kafkaTemplate.send("inventory-reserved",
              orderId,
              Map.of(
                "eventId", UUID.randomUUID().toString(),
                "orderId", orderId,
                "productId", productId,
                "quantity", quantity
              ));
        } else {
          kafkaTemplate.send("inventory-rejected",
            orderId,
            Map.of(
              "eventId", UUID.randomUUID().toString(),
              "orderId", orderId,
              "reason", "INSUFFICIENT_STOCK"
            ));
        }
    }
}
