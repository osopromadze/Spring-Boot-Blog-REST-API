package com.sopromadze.blogapi.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.category.Category;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public PagedResponse<Category> getAllCategories(int page, int size){
		AppUtils.validatePageNumberAndSize(page, size);
		
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		
		Page<Category> categories = categoryRepository.findAll(pageable);
		
		List<Category> content = categories.getNumberOfElements() == 0 ? Collections.emptyList() : categories.getContent();
		
		return new PagedResponse<Category>(content, categories.getNumber(), categories.getSize(), categories.getTotalElements(), categories.getTotalPages(), categories.isLast());
	}
	
	public ResponseEntity<?> getCategory(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
	
	public ResponseEntity<?> addCategory(Category category, UserPrincipal currentUser){
        Category newCategory =  categoryRepository.save(category);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }
	
	public ResponseEntity<?> updateCategory(Long id, Category newCategory, UserPrincipal currentUser){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (category.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            category.setName(newCategory.getName());
            Category updatedCategory = categoryRepository.save(category);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to edit this category"), HttpStatus.UNAUTHORIZED);
    }
	
	public ResponseEntity<?> deleteCategory(Long id, UserPrincipal currentUser){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("category", "id", id));
        if (category.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            categoryRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted category"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(true, "You don't have permission to delete this category"), HttpStatus.UNAUTHORIZED);
    }
}






















