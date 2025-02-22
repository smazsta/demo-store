package com.example.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private BigDecimal price;

  private int stock;

  public Product() {
    // empty
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
    return "Product{" + "id=" + id + ", name='" + name + '\'' + ", price=" + price + ", stock=" + stock + '}';
  }

  public static class ProductBuilder {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;

    public ProductBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ProductBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProductBuilder price(BigDecimal price) {
      this.price = price;
      return this;
    }

    public ProductBuilder stock(int stock) {
      this.stock = stock;
      return this;
    }

    public Product build() {
      Product product = new Product();
      product.setId(id);
      product.setName(name);
      product.setPrice(price);
      product.setStock(stock);
      return product;
    }
  }
}
