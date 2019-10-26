package com.sopromadze.blogapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
