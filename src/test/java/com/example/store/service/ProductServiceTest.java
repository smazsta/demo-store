package com.example.store.service;

import com.example.store.dto.PageableProductDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.model.Product;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Nested
  class Add_Product {
    @Test
    @DisplayName("Should save product in repository")
    void shouldSaveProductInRepositoryTest() {
      Product expected = new Product("banana", BigDecimal.TEN, 10);

      when(productRepository.save(any(Product.class))).thenReturn(expected);

      Product actual = productService.addProduct(expected);

      assertThat(actual).isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }
  }

  @Nested
  class Get_Products {
    @Test
    @DisplayName("Should get products for valid paging arguments")
    void shouldUseDefaultPagingTest() {
      Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
      Page<Product> productPage = new PageImpl<>(List.of(
          new Product("banana", BigDecimal.TEN, 10),
          new Product("apple", BigDecimal.ONE, 5)
      ));

      when(productRepository.findAll(pageable)).thenReturn(productPage);

      PageableProductDTO response = productService.getProducts(pageable);

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

      verify(productRepository, never()).findAll(pageable);
    }
  }
}