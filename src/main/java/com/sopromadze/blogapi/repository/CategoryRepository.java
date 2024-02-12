package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface CategoryRepository {

  Page<Category> findAll(Pageable pageable);

  Optional<Category> findById(Long id);

  Category save(Category category);

  void deleteById(Long id);

}
