package com.example.store.dto;

import java.math.BigDecimal;

public class ProductDTO {

  private String name;

  private BigDecimal price;

  private int stock;

  public ProductDTO(String name, BigDecimal price, int stock) {
    this.name = name;
    this.price = price;
    this.stock = stock;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }
}
