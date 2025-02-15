package com.example.store.service;

import com.example.store.dto.PageableProductDTO;
import com.example.store.dto.ProductDTO;
import com.example.store.mapper.ProductMapper;
import com.example.store.model.Product;
import com.example.store.repository.ProductRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

  private static final List<String> VALID_SORT_FIELDS = List.of("name", "price", "stock");
  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @CacheEvict(allEntries = true)
  public Product addProduct(Product product) {
    return productRepository.save(product);
  }

  @Cacheable(key = "T(com.example.store.cache.PageableCacheKey).of(#p0)")
  public PageableProductDTO getProducts(Pageable pageable) throws IllegalArgumentException {
    validatePageable(pageable);
    Page<Product> productPage = productRepository.findAll(pageable);
    List<ProductDTO> productDTOs = productPage.getContent().stream()
        .map(ProductMapper::toProductDTO)
        .toList();

    return new PageableProductDTO(
        productDTOs,
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
}
