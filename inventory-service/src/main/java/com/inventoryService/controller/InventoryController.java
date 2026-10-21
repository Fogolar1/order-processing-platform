package com.inventoryService.controller;

import com.inventoryService.model.StockEntity;
import com.inventoryService.records.StockUpdateRecord;
import com.inventoryService.repository.StockRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

  private final StockRepository stockRepository;

  @PutMapping("/{productId}")
  public StockEntity updateStock(@PathVariable String productId, @Valid @RequestBody StockUpdateRecord req) {
      StockEntity s = stockRepository.findById(productId).orElse(new StockEntity(productId, req.available()));

      s.setAvailable(req.available());

      return stockRepository.save(s);
  }

  @GetMapping("/{productId}")
  public StockEntity getStock(@PathVariable String productId) {
      return stockRepository.findById(productId).orElseThrow();
  }

  @GetMapping
  public List<StockEntity> getStocks() {
      return stockRepository.findAll();
  }
}
