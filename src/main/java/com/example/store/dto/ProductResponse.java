package com.example.store.dto;

import java.math.BigDecimal;

public class ProductResponse {

  private final Long id;

  private final String name;

  private final BigDecimal price;

  private final int stock;

  public ProductResponse(Long id, String name, BigDecimal price, int stock) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.stock = stock;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getStock() {
    return stock;
  }

  @Override
  public String toString() {
    return "ProductResponse{" + "uuid='" + id + '\'' + ", name='" + name + '\'' + ", price=" + price + ", stock=" + stock + '}';
  }
}

