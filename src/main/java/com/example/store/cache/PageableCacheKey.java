package com.example.store.cache;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class PageableCacheKey {

  private final int pageNumber;
  private final int pageSize;
  private final Sort sort;

  private PageableCacheKey(int pageNumber, int pageSize, Sort sort) {
    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
    this.sort = sort;
  }

  public static PageableCacheKey of(Pageable pageable) {
    return new PageableCacheKey(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PageableCacheKey that = (PageableCacheKey) o;
    return pageNumber == that.pageNumber &&
        pageSize == that.pageSize &&
        Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageNumber, pageSize, sort);
  }
}
