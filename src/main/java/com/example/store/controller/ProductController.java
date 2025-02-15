package com.example.store.controller;

import com.example.store.dto.PageableProductDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.model.Product;
import com.example.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<Product> addProduct(@RequestBody @Valid Product product) {
    return ResponseEntity.ok(productService.addProduct(product));
  }

  @GetMapping
  public ResponseEntity<PageableProductDTO> getProducts(
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(productService.getProducts(pageable));
  }
}
