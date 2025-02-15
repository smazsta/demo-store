package com.example.store.service;

import com.example.store.cache.PageableCacheKey;
import com.example.store.dto.PageableProductDTO;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProductServiceCacheTest {

  @MockitoBean
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private CacheManager cacheManager;

  private Cache cache;

  @BeforeEach
  void setup() {
    cache = cacheManager.getCache("products");
    assertNotNull(cache, "Cache 'products' should not be null");
    cache.clear();
  }

  @Nested
  class Add_Product {
    @Test
    @DisplayName("Should evict products from the cache")
    void shouldAddProductEvictCacheTest() {
      PageRequest pageable1 = PageRequest.of(0, 10, Sort.by("name"));
      PageRequest pageable2 = PageRequest.of(1, 10, Sort.by("price"));

      Product product1 = new Product("Product 1", BigDecimal.valueOf(99.99), 10);
      Product product2 = new Product("Product 2", BigDecimal.valueOf(49.99), 5);
      List<Product> products1 = List.of(product1);
      List<Product> products2 = List.of(product2);
      Page<Product> productPage1 = new PageImpl<>(products1, pageable1, products1.size());
      Page<Product> productPage2 = new PageImpl<>(products2, pageable2, products2.size());

      when(productRepository.findAll(pageable1)).thenReturn(productPage1);
      when(productRepository.findAll(pageable2)).thenReturn(productPage2);

      productService.getProducts(pageable1);
      productService.getProducts(pageable2);

      Cache cache = cacheManager.getCache("products");
      assertNotNull(cache);

      PageableCacheKey cacheKey1 = PageableCacheKey.of(pageable1);
      PageableCacheKey cacheKey2 = PageableCacheKey.of(pageable2);
      assertNotNull(cache.get(cacheKey1));
      assertNotNull(cache.get(cacheKey2));

      Product newProduct = new Product("New Product", BigDecimal.valueOf(199.99), 20);
      when(productRepository.save(newProduct)).thenReturn(newProduct);
      productService.addProduct(newProduct);

      assertNull(cache.get(cacheKey1));
      assertNull(cache.get(cacheKey2));
    }
  }

  @Nested
  class Get_Products {
    @Test
    @DisplayName("Should cache product results")
    void shouldGetProductsCacheTest() {
      PageRequest pageable = PageRequest.of(0, 10, Sort.by("name"));
      Product product1 = new Product("Product 1", BigDecimal.valueOf(99.99), 10);
      Product product2 = new Product("Product 2", BigDecimal.valueOf(49.99), 5);
      List<Product> products = List.of(product1, product2);
      Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

      when(productRepository.findAll(pageable)).thenReturn(productPage);

      PageableProductDTO result1 = productService.getProducts(pageable);
      PageableProductDTO result2 = productService.getProducts(pageable);

      assertEquals(result1, result2);

      verify(productRepository, times(1)).findAll(pageable);

      Cache cache = cacheManager.getCache("products");
      assertNotNull(cache);

      PageableCacheKey cacheKey = PageableCacheKey.of(pageable);
      assertNotNull(cache.get(cacheKey));
    }
  }
}
