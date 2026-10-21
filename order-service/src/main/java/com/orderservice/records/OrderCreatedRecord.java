package com.orderservice.records;

import java.math.BigDecimal;

public record OrderCreatedRecord(
  String eventId,
  String orderId,
  String productId,
  int quantity,
  BigDecimal amount
) {
}
