package com.example.store.service;

import com.example.store.dto.ProductPage;
import com.example.store.dto.ProductRequest;
import com.example.store.dto.ProductResponse;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import jakarta.validation.constraints.Positive;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

  private static final List<String> VALID_SORT_FIELDS = List.of("name", "price", "stock");
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @CacheEvict(allEntries = true)
  public ProductResponse addProduct(ProductRequest productRequest) {
    Product product = productMapper.toProduct(productRequest);
    Product savedProduct = productRepository.save(product);
    return productMapper.toProductResponse(savedProduct);
  }

  @Cacheable(value = "product", key = "#p0")
  public ProductResponse getProduct(String name) {
    return productRepository.findByName(name)
        .map(productMapper::toProductResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
  }

  @Cacheable(value = "products", key = "T(com.example.store.cache.PageableCacheKey).of(#p0)")
  public ProductPage getProducts(Pageable pageable) throws IllegalArgumentException {
    validatePageable(pageable);
    Page<Product> productPage = productRepository.findAll(pageable);
    List<ProductResponse> productResponses = productPage.getContent().stream()
        .map(productMapper::toProductResponse)
        .toList();

    return new ProductPage(
        productResponses,
        productPage.getNumber(),
        productPage.getSize(),
        productPage.getTotalElements(),
        productPage.getTotalPages()
    );
  }

  private static void validatePageable(Pageable pageable) {
    Sort sort = pageable.getSort();
    for (Sort.Order order : sort) {
      if (!VALID_SORT_FIELDS.contains(order.getProperty())) {
        throw new IllegalArgumentException("Invalid sort field: " + order.getProperty());
      }
    }
  }

  @CacheEvict(key = "#p0")
  public ProductResponse updateStock(String name, int stock) {
    Product product = productRepository.findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    product.setStock(stock);
    Product updatedProduct = productRepository.save(product);
    return productMapper.toProductResponse(updatedProduct);
  }
}
