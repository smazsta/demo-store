package com.example.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Product {

  @Id
  private String uuid;

  @NotNull(message = "Product name cannot be null")
  @NotBlank
  @Size(min = 1, max = 100, message = "Invalid product name size")
  private String name;

  // todo add check for decimal .2
  @NotNull(message = "Price cannot be null")
  @Positive(message = "Price must be a positive number")
  private BigDecimal price;

  @PositiveOrZero
  private int stock;

  public Product() {
    // empty
  }

  public Product(String name, BigDecimal price, int stock) {
    this.name = name;
    this.price = price;
    this.stock = stock;
  }

  @PrePersist
  private void setUuid() {
    if (this.uuid == null) {
      this.uuid = UUID.randomUUID().toString();
    }
  }

  public String getUuid() {
    return uuid;
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

  @Override
  public String toString() {
    return "Product{" +
        "uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", price=" + price +
        ", stock=" + stock +
        '}';
  }
}
