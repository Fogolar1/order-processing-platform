package com.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "order_id"))
public class PaymentEntity {

  @Id
  private UUID id;

  @Column(name = "order_id", nullable = false, unique = true)
  private String orderId;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private Instant createdAt;

  public PaymentEntity(String orderId, BigDecimal amount, String status) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createdAt = Instant.now();
  }
}
