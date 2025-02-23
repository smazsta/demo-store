package com.example.store.controller;

import com.example.store.dto.ProductPage;
import com.example.store.dto.ProductRequest;
import com.example.store.dto.ProductResponse;
import com.example.store.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
  public ResponseEntity<ProductResponse> addProduct(@RequestBody @Valid ProductRequest request) {
    return ResponseEntity.ok(productService.addProduct(request));
  }

  @GetMapping("/{name}")
  public ResponseEntity<ProductResponse> getProduct(@PathVariable String name) {
    return ResponseEntity.ok(productService.getProduct(name));
  }

  @GetMapping
  public ResponseEntity<ProductPage> getProducts(
      @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.ok(productService.getProducts(pageable));
  }

  @PatchMapping("/{name}/stock")
  public ResponseEntity<ProductResponse> updateStock(@PathVariable("name") String name, @RequestParam("stock") @Min(0) int stock) {
    ProductResponse updatedProduct = productService.updateStock(name, stock);
    return ResponseEntity.ok(updatedProduct);
  }
}
