package com.example.store.mapper;

import com.example.store.dto.ProductRequest;
import com.example.store.dto.ProductResponse;
import com.example.store.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  public Product toProduct(ProductRequest productRequest) {
    Product product = new Product();
    product.setName(productRequest.getName());
    product.setPrice(productRequest.getPrice());
    product.setStock(productRequest.getStock());
    return product;
  }

  public ProductResponse toProductResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getStock()
    );
  }
}
