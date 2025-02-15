package com.example.store.repository;

import com.example.store.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("Should save Product in repository")
  void shouldSaveProductTest() {
    Product expected = new Product("banana", BigDecimal.TEN, 10);

    Product actual = productRepository.save(expected);

    assertThat(actual).isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}