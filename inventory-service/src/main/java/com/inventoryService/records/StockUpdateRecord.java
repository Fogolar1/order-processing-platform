package com.inventoryService.records;

import jakarta.validation.constraints.Min;

public record StockUpdateRecord(
  @Min(0) int available
) {
}
