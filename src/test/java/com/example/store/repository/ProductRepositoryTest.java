package com.example.store.repository;

import com.example.store.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("Should save Product in repository")
  void shouldSaveProductTest() {
    Product expected = new Product.ProductBuilder()
        .name("Banana")
        .price(BigDecimal.valueOf(0.99))
        .stock(100)
        .build();

    Product actual = productRepository.save(expected);

    assertThat(actual).isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}