package com.orderservice.records;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateOrderRecord(
  @NotBlank String productId,
  @Min(1) int quantity,
  @DecimalMin("0.01")BigDecimal amount
  ) {
}
