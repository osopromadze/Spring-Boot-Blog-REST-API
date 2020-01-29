package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.exception.UnathorizedException;
import com.sopromadze.blogapi.model.category.Category;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface CategoryService {

	PagedResponse<Category> getAllCategories(int page, int size);

	ResponseEntity<Category> getCategory(Long id);

	ResponseEntity<Category> addCategory(Category category, UserPrincipal currentUser);

	ResponseEntity<Category> updateCategory(Long id, Category newCategory, UserPrincipal currentUser)
			throws UnathorizedException;

	ResponseEntity<ApiResponse> deleteCategory(Long id, UserPrincipal currentUser) throws UnathorizedException;

}