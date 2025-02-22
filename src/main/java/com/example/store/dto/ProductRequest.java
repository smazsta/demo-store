package com.example.store.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductRequest {

  @NotNull(message = "Product name cannot be null")
  @NotBlank
  @Size(min = 1, max = 100, message = "Invalid product name size")
  private final String name;

  // todo add check for decimal .2
  @NotNull(message = "Price cannot be null")
  @Positive(message = "Price must be a positive number")
  private final BigDecimal price;

  @PositiveOrZero
  private final int stock;

  public ProductRequest(String name, BigDecimal price, int stock) {
    this.name = name;
    this.price = price;
    this.stock = stock;
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
    return "ProductRequest{" + "name='" + name + '\'' + ", price=" + price + ", stock=" + stock + '}';
  }
}
