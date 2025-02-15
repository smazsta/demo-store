package com.example.store.dto;

import java.util.List;

public class PageableProductDTO {

  List<ProductDTO> content;

  private int page;

  private int size;

  private long totalElements;

  private long totalPages;

  public PageableProductDTO(List<ProductDTO> content, int page, int size,
      long totalElements, long totalPages) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
  }

  public List<ProductDTO> getContent() {
    return content;
  }

  public void setContent(List<ProductDTO> content) {
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
