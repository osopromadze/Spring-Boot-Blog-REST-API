package com.sopromadze.blogapi.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.category.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
    
}
