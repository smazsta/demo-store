package com.example.store.service;

import com.example.store.cache.PageableCacheKey;
import com.example.store.dto.ProductPage;
import com.example.store.dto.ProductRequest;
import com.example.store.dto.ProductResponse;
import com.example.store.mapper.ProductMapper;
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
import java.util.Optional;

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

  @MockitoBean
  private ProductMapper productMapper;

  @BeforeEach
  void setup() {
    assertNotNull(cacheManager.getCache("page"), "Cache 'products' should not be null");
    assertNotNull(cacheManager.getCache("single"), "Cache 'product' should not be null");
    cacheManager.getCache("page").clear();
    cacheManager.getCache("single").clear();
  }

  @Nested
  class Add_Product {
    @Test
    @DisplayName("Should evict products from the cache")
    void shouldAddProductEvictCacheTest() {
      PageRequest pageable1 = PageRequest.of(0, 10, Sort.by("name"));
      PageRequest pageable2 = PageRequest.of(1, 10, Sort.by("price"));

      Product product1 = new Product.ProductBuilder()
          .id(1L)
          .name("Product 1")
          .price(BigDecimal.valueOf(99.99))
          .stock(10)
          .build();
      Product product2 = new Product.ProductBuilder()
          .id(2L)
          .name("Product 2")
          .price(BigDecimal.valueOf(49.99))
          .stock(5)
          .build();
      List<Product> products1 = List.of(product1);
      List<Product> products2 = List.of(product2);
      Page<Product> productPage1 = new PageImpl<>(products1, pageable1, products1.size());
      Page<Product> productPage2 = new PageImpl<>(products2, pageable2, products2.size());

      when(productRepository.findAll(pageable1)).thenReturn(productPage1);
      when(productRepository.findAll(pageable2)).thenReturn(productPage2);

      productService.getProducts(pageable1);
      productService.getProducts(pageable2);

      Cache pageCache = cacheManager.getCache("page");
      assertNotNull(pageCache);
      Cache singleCache = cacheManager.getCache("single");
      assertNotNull(singleCache);

      PageableCacheKey cacheKey1 = PageableCacheKey.of(pageable1);
      PageableCacheKey cacheKey2 = PageableCacheKey.of(pageable2);
      assertNotNull(pageCache.get(cacheKey1));
      assertNotNull(pageCache.get(cacheKey2));

      ProductRequest request = new ProductRequest("New Product", BigDecimal.valueOf(199.99), 20);
      Product newProduct = new Product.ProductBuilder()
          .id(32L)
          .name(request.getName())
          .price(request.getPrice())
          .stock(request.getStock())
          .build();
      when(productRepository.save(newProduct)).thenReturn(newProduct);
      when(productMapper.toProduct(request)).thenReturn(newProduct);
      productService.addProduct(request);

      assertNull(pageCache.get(cacheKey1));
      assertNull(pageCache.get(cacheKey2));
      assertNotNull(singleCache.get(request.getName()));
    }
  }

  @Nested
  class Get_Products {
    @Test
    @DisplayName("Should cache product results")
    void shouldGetProductsCacheTest() {
      PageRequest pageable = PageRequest.of(0, 10, Sort.by("name"));
      Product product1 = new Product.ProductBuilder()
          .id(1L)
          .name("Product 1")
          .price(BigDecimal.valueOf(99.99))
          .stock(10)
          .build();

      Product product2 = new Product.ProductBuilder()
          .id(2L)
          .name("Product 2")
          .price(BigDecimal.valueOf(49.99))
          .stock(5)
          .build();
      List<Product> products = List.of(product1, product2);
      Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

      when(productRepository.findAll(pageable)).thenReturn(productPage);

      ProductPage result1 = productService.getProducts(pageable);
      ProductPage result2 = productService.getProducts(pageable);

      assertEquals(result1, result2);

      verify(productRepository, times(1)).findAll(pageable);

      Cache cache = cacheManager.getCache("page");
      assertNotNull(cache);

      PageableCacheKey cacheKey = PageableCacheKey.of(pageable);
      assertNotNull(cache.get(cacheKey));
    }
  }

  @Nested
  class Update_Product {
    @Test
    @DisplayName("Should evict product from the cache")
    void updateProduct_evictCacheTest() {
      Product product = new Product.ProductBuilder()
          .id(1L)
          .name("Onion")
          .price(BigDecimal.valueOf(99.99))
          .stock(10)
          .build();

      ProductRequest productRequest = new ProductRequest(product.getName(), product.getPrice(), product.getStock());
      ProductResponse productResponse = new ProductResponse(product.getId(), product.getName(), product.getPrice(),
          product.getStock());

      when(productRepository.save(any(Product.class))).thenReturn(product);
      when(productMapper.toProductResponse(product)).thenReturn(productResponse);

      productService.addProduct(productRequest);

      Cache cache = cacheManager.getCache("single");
      assertNotNull(cache);
      assertNotNull(cache.get(product.getName()));

      Product updated = new Product.ProductBuilder()
          .id(1L)
          .name("Onion")
          .price(BigDecimal.valueOf(99.99))
          .stock(16)
          .build();
      ProductResponse updatedResponse = new ProductResponse(updated.getId(), updated.getName(), updated.getPrice(),
          updated.getStock());

      when(productRepository.save(updated)).thenReturn(updated);
      when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));
      when(productMapper.toProductResponse(updated)).thenReturn(updatedResponse);

      productService.updateStock(product.getName(), updated.getStock());

      assertNull(cache.get(product.getName()));
    }
  }
}
