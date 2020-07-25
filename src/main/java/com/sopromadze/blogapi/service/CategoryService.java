package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.payload.CategoryRequest;
import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.category.Category;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface CategoryService {

	PagedResponse<Category> getAllCategories(int page, int size);

	ResponseEntity<Category> getCategory(Long id);

	ResponseEntity<Category> addCategory(CategoryRequest categoryRequest, UserPrincipal currentUser);

	ResponseEntity<Category> updateCategory(Long id, CategoryRequest categoryRequest, UserPrincipal currentUser)
			throws UnauthorizedException;

	ResponseEntity<ApiResponse> deleteCategory(Long id, UserPrincipal currentUser) throws UnauthorizedException;

}
