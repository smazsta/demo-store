package com.example.store.controller;

import com.example.store.exception.ResourceNotFoundException;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductController productController;

  @Nested
  class POST_addProduct {
    @Test
    @DisplayName("Should return created product")
    void createProductTest() throws Exception {
      Product product = new Product();
      product.setName("Banana");
      product.setPrice(BigDecimal.valueOf(1.99));
      product.setStock(10);
      String json = objectMapper.writeValueAsString(product);

      mockMvc.perform(post("/products")
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .content(json))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.name").value(product.getName()))
          .andExpect(jsonPath("$.price").value(product.getPrice()))
          .andExpect(jsonPath("$.stock").value(product.getStock()));
    }

    @ParameterizedTest(name = "Should return Bad Request when {1}")
    @MethodSource("invalidProductGenerator")
    void shouldReturnExceptionForInvalidProductTest(String message, Product product) throws Exception{
      String json = objectMapper.writeValueAsString(product);

      mockMvc.perform(post("/products")
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .content(json))
          .andExpect(status().isBadRequest());
    }

    public static Stream<Arguments> invalidProductGenerator() {
      return Stream.of(
          Arguments.of("name is null", new Product.ProductBuilder().name(null).price(BigDecimal.TEN).stock(10).build()),
          Arguments.of("name is empty", new Product.ProductBuilder().name("").price(BigDecimal.TEN).stock(10).build()),
          Arguments.of("name is blank", new Product.ProductBuilder().name(" ").price(BigDecimal.TEN).stock(10).build()),
          Arguments.of("price is negative", new Product.ProductBuilder().name("potato").price(BigDecimal.valueOf(-10L)).stock(10).build()),
          Arguments.of("price is null", new Product.ProductBuilder().name("potato").price(null).stock(10).build()),
          Arguments.of("stock is negative", new Product.ProductBuilder().name("potato").price(BigDecimal.TEN).stock(-10).build())
      );
    }
  }

  @Nested
  class GET_pageableProduct {
    @Test
    @DisplayName("Should return paginated and sorted products")
    void getPageableProductsTest() throws Exception {
      productRepository.save(new Product.ProductBuilder().name("Banana").price(BigDecimal.valueOf(0.99)).stock(10).build());
      productRepository.save(new Product.ProductBuilder().name("Apple").price(BigDecimal.valueOf(1.99)).stock(50).build());

      mockMvc.perform(get("/products")
              .param("page", "0")
              .param("size", "10")
              .param("sort", "name,asc")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].name").value("Apple"))
          .andExpect(jsonPath("$.content[1].name").value("Banana"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    @ParameterizedTest(name = "Should return IllegalArgumentException when {0}")
    @MethodSource("invalidPageableGenerator")
    void invalidPageableParameters(String testName, String page, String size, String sort,
        String errorMessage) throws Exception {
      mockMvc.perform(get("/products")
              .param("page", page)
              .param("size", size)
              .param("sort", sort)
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(result -> {
            Exception exception = result.getResolvedException();
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
            assertEquals(errorMessage, exception.getMessage());
          });
    }

    public static Stream<Arguments> invalidPageableGenerator() {
      return Stream.of(
          Arguments.of("sort has invalid sort field", "0", "10", "invalidField,desc", "Invalid sort field: invalidField"),
          Arguments.of("sort has invalid direction", "0", "10", "name,invalidDirection", "Invalid sort field: invalidDirection")
      );
    }

    @ParameterizedTest(name = "Should automatically handle invalid page arguments when {0}")
    @MethodSource("handledInvalidPageableGenerator")
    void handleInvalidPageableParameters(String testName, String page, String size, String sort,
        String errorMessage) throws Exception {
      productRepository.save(new Product.ProductBuilder().name("Banana").price(BigDecimal.valueOf(0.99)).stock(100).build());
      productRepository.save(new Product.ProductBuilder().name("Apple").price(BigDecimal.valueOf(1.99)).stock(50).build());

      mockMvc.perform(get("/products")
              .param("page", page)
              .param("size", size)
              .param("sort", sort)
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].name").value("Apple"))
          .andExpect(jsonPath("$.content[1].name").value("Banana"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1));
    }

    public static Stream<Arguments> handledInvalidPageableGenerator() {
      return Stream.of(
          Arguments.of("page is negative", "-1", "10", "name,asc", "Page number must be non-negative"),
          Arguments.of("size is negative", "0", "-2", "name,asc", "Page size must be positive"),
          Arguments.of("size is 0", "0", "0", "name,asc", "Page size must be positive"),
          Arguments.of("sort has no sort field", "0", "10", "desc", "Invalid sort field: "),
          Arguments.of("sort has no direction", "0", "10", "name", "Invalid sort direction: ")
      );
    }
  }

  @Nested
  class PATCH_updateProduct {

    @Test
    @DisplayName("Should return updated product")
    void createProductTest() throws Exception {
      Product cached = new Product.ProductBuilder()
          .name("car")
          .price(BigDecimal.valueOf(5.99))
          .stock(3)
          .build();

      productRepository.save(cached);

      mockMvc.perform(patch("/products/" + cached.getName() + "/stock")
              .param("stock", "14")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.name").value(cached.getName()))
          .andExpect(jsonPath("$.price").value(cached.getPrice()))
          .andExpect(jsonPath("$.stock").value(14));
    }

    @Test
    @DisplayName("Update stock - Invalid stock value")
    void updateStock_InvalidStock() throws Exception {
      mockMvc.perform(patch("/products/Test Product/stock")
              .param("stock", "-10")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update stock - Product not found")
    void updateStock_ProductNotFound() throws Exception {
      Optional<Product> product = productRepository.findByName("non-existent");
      assertThat(product).isEmpty();

      mockMvc.perform(patch("/products/non-existent/stock")
              .param("stock", "50")
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}