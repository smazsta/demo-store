package com.example.store.dto;

import java.util.List;

public class ProductPage {

  private List<ProductResponse> content;

  private int page;

  private int size;

  private long totalElements;

  private long totalPages;

  public ProductPage(List<ProductResponse> content, int page, int size,
      long totalElements, long totalPages) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
  }

  public List<ProductResponse> getContent() {
    return content;
  }

  public void setContent(List<ProductResponse> content) {
    this.content = content;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public long getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(long totalPages) {
    this.totalPages = totalPages;
  }
}
