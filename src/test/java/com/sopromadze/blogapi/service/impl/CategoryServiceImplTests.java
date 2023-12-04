package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class CategoryServiceImplTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AppUtils appUtils;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void getAllCategories_whenNoElementsFound_thenReturnEmptyList() {
        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Category> response = categoryService.getAllCategories(1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getAllCategories_whenElementsFound_returnContent() {
        // Mock the Page interface using Mockito.mock
        Page<Category> page = Mockito.mock(Page.class);

        // Configure the behavior of the mock
        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createCategoryList());

        // Mock the repository method to return the mock Page
        Mockito.when(categoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        // Call the service method
        PagedResponse<Category> response = categoryService.getAllCategories(1, 2);

        // Assert the results
        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getCategory_whenNoResourceFound_thenThrowException() {
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        categoryService.getCategory(1L);
    }

    @Test
    public void getCategory_whenCategoryFound_thenReturnCategory() {
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Category()));
        ResponseEntity<Category> response = categoryService.getCategory(1L);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateCategory_whenNoResourceFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        categoryService.updateCategory(1L, new Category(), fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updateCategory_whenUserUnauthorized_thenThrowException() {
        try {
            Category categoryParam = new Category();
            Category returnCategory = new Category();
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            fakeUser.setId(1L);
            User categoryFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            returnCategory.setCreatedBy(2L);
            categoryParam.setCreatedBy(1L);

            Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(returnCategory));

            categoryService.updateCategory(1L, categoryParam, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void updateCategory_whenCategoryUpdated_thenReturnSuccessResponse() {
        Category categoryParam = new Category();
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        fakeUser.setId(1L);
        User categoryFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        categoryParam.setCreatedBy(1L);

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(categoryParam));

        ResponseEntity<?> response = categoryService.updateCategory(1L, categoryParam, fakeUserPrincipal);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteCategory_whenNoResourceFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        categoryService.deleteCategory(1L, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void deleteCategory_whenUserUnauthorized_thenThrowException() {
        try {
            Category testCategory = new Category();
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            fakeUser.setId(1L);
            User categoryFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            testCategory.setCreatedBy(2L);

            Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testCategory));
            categoryService.deleteCategory(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void deleteCategory_whenCategoryDeleted_thenReturnSuccessResponse() {
        Category categoryParam = new Category();
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        fakeUser.setId(1L);
        User categoryFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        categoryParam.setCreatedBy(1L);

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(categoryParam));

        ResponseEntity<ApiResponse> response = categoryService.deleteCategory(1L, fakeUserPrincipal);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    // Helper method to create a list of categories
    private List<Category> createCategoryList() {
        List<Category> list = new ArrayList<>();
        Category category = new Category();
        list.add(category);
        return list;
    }
}
