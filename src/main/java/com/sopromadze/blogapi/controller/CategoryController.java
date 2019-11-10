package com.sopromadze.blogapi.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sopromadze.blogapi.model.category.Category;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CategoryService;
import com.sopromadze.blogapi.utils.AppConstants;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	@Autowired
    private CategoryService categoryService;

    @GetMapping
    public PagedResponse<?> getAllCategories(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return categoryService.getAllCategories(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addCategory(@Valid @RequestBody Category category, @CurrentUser UserPrincipal currentUser){
        return categoryService.addCategory(category, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable(name = "id") Long id){
        return categoryService.getCategory(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable(name = "id") Long id, @Valid @RequestBody Category category, @CurrentUser UserPrincipal currentUser){
        return categoryService.updateCategory(id, category, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return categoryService.deleteCategory(id, currentUser);
    }

}
