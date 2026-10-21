package com.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    public OrderEntity(String productId, int quantity, BigDecimal amount){
      this.id = UUID.randomUUID();
      this.productId = productId;
      this.quantity = quantity;
      this.amount = amount;
      this.status = "PENDING_INVENTORY";
      this.createdAt = Instant.now();
    }

}
