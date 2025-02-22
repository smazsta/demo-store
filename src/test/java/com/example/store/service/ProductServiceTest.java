package com.example.store.service;

import com.example.store.dto.ProductPage;
import com.example.store.dto.ProductRequest;
import com.example.store.dto.ProductResponse;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.model.Product;
import com.example.store.model.Product.ProductBuilder;
import com.example.store.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Mock
  private ProductMapper productMapper;

  @Nested
  @DisplayName("Creating product")
  class Add_Product {
    @Test
    @DisplayName("Should save product in repository")
    void shouldSaveProductInRepositoryTest() {
      ProductRequest request = new ProductRequest("banana", BigDecimal.TEN, 10);
      Product product = new ProductBuilder()
          .id(2L)
          .name(request.getName())
          .price(request.getPrice())
          .stock(request.getStock())
          .build();

      ProductResponse productResponse = new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());

      when(productMapper.toProduct(request)).thenReturn(product);
      when(productRepository.save(product)).thenReturn(product);
      when(productMapper.toProductResponse(product)).thenReturn(productResponse);

      ProductResponse actual = productService.addProduct(request);

      assertThat(actual).isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(productResponse);
    }
  }

  @Nested
  @DisplayName("Getting pageable product")
  class Get_Products {
    @Test
    @DisplayName("Should get products for valid paging arguments")
    void shouldUseDefaultPagingTest() {
      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
      Product product1 = new ProductBuilder()
          .id(1L)
          .name("banana")
          .price(BigDecimal.valueOf(10))
          .stock(10)
          .build();
      Product product2 = new ProductBuilder()
          .id(2L)
          .name("apple")
          .price(BigDecimal.valueOf(1))
          .stock(5)
          .build();
      Page<Product> productPage = new PageImpl<>(List.of(product1, product2));

      ProductResponse productResponse1 = new ProductResponse(
          product1.getId(), product1.getName(), product1.getPrice(), product1.getStock());
      ProductResponse productResponse2 = new ProductResponse(
          product2.getId(), product2.getName(), product2.getPrice(), product2.getStock());

      when(productRepository.findAll(pageable)).thenReturn(productPage);
      when(productMapper.toProductResponse(product1)).thenReturn(productResponse1);
      when(productMapper.toProductResponse(product2)).thenReturn(productResponse2);

      ProductPage response = productService.getProducts(pageable);

      assertThat(response).isNotNull();
      assertThat(response.getContent()).hasSize(2);
      assertThat(response.getContent().get(0).getName()).isEqualTo("banana");
      assertThat(response.getContent().get(1).getName()).isEqualTo("apple");
      assertThat(response.getPage()).isEqualTo(0);
      assertThat(response.getSize()).isEqualTo(2);
      assertThat(response.getTotalElements()).isEqualTo(2);
      assertThat(response.getTotalPages()).isEqualTo(1);

      verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid paging arguments")
    void shouldThrowExceptionForInvalidPageableArgumentsTest() {
      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "invalidField"));

      assertThatThrownBy(() -> productService.getProducts(pageable))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Invalid sort field: invalidField");

      verifyNoMoreInteractions(productRepository);
      verifyNoInteractions(productMapper);
    }
  }

  @Nested
  @DisplayName("Updating product")
  class Update_Product {
    @Test
    @DisplayName("Should update product in repository")
    void shouldUpdateProductInRepositoryTest() {
      Product cached = new ProductBuilder()
          .id(2L)
          .name("banana")
          .price(BigDecimal.valueOf(3.99))
          .stock(10)
          .build();

      Product updated = new ProductBuilder()
          .id(2L)
          .name("banana")
          .price(BigDecimal.valueOf(3.99))
          .stock(30)
          .build();

      ProductResponse productResponse = new ProductResponse(updated.getId(), updated.getName(), updated.getPrice(), updated.getStock());

      when(productRepository.findByName(cached.getName())).thenReturn(Optional.of(cached));
      when(productRepository.save(any())).thenReturn(updated);
      when(productMapper.toProductResponse(updated)).thenReturn(productResponse);

      ProductResponse actual = productService.updateStock(cached.getName(), updated.getStock());

      assertThat(actual)
          .isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(productResponse);

      verify(productRepository, times(1)).save(any());
      verify(productMapper, times(1)).toProductResponse(any());
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFoundTest() {
      when(productRepository.findByName(any())).thenReturn(Optional.empty());

      assertThatThrownBy(() -> productService.updateStock("random", 30))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Product not found");

      verify(productRepository, times(1)).findByName("random");
      verifyNoMoreInteractions(productRepository);
      verifyNoInteractions(productMapper);
    }
  }
}