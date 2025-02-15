package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.model.Product;

// todo use OpenFeign instead
public class ProductMapper {
  public static ProductDTO toProductDTO(Product product) {
    return new ProductDTO(product.getName(), product.getPrice(), product.getStock());
  }
}
