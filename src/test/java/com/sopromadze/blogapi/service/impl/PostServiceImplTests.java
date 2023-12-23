package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class PostServiceImplTests {

    @Mock
    private AppUtils appUtils;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void getAllPosts_whenNoPostsFound_thenReturnEmptyList() {
        Mockito.when(postRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Post> response = postService.getAllPosts(1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getAllPosts_whenPostsFound_thenReturnContent() {
        Page<Post> page = Mockito.mock(Page.class);

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createPostList());

        Mockito.when(postRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PagedResponse<Post> response = postService.getAllPosts(1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test
    public void getPostsByCreatedBy_whenNoPostsFound_thenReturnEmptyList() {
        User fakeUser = new User();
        fakeUser.setId(1L);
        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(fakeUser);
        Mockito.when(postRepository.findByCreatedBy(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Post> response = postService.getPostsByCreatedBy("john.doe", 1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getPostsByCreatedBy_whenPostsFound_thenReturnContent() {
        Page<Post> page = Mockito.mock(Page.class);
        User fakeUser = new User();
        fakeUser.setId(1L);

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createPostList());

        Mockito.when(userRepository.getUserByName(Mockito.anyString())).thenReturn(fakeUser);
        Mockito.when(postRepository.findByCreatedBy(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(page);

        PagedResponse<Post> response = postService.getPostsByCreatedBy("john.doe", 1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPostsByCategory_whenCategoryNotFound_thenThrowException() {
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.getPostsByCategory(1L, 1, 2);
    }

    @Test
    public void getPostsByCategory_whenNoPostsFound_thenReturnEmptyList() {
        Category category = new Category();
        category.setId(1L);

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));
        Mockito.when(postRepository.findByCategory(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Post> response = postService.getPostsByCategory(1L, 1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getPostsByCategory_whenPostsFound_thenReturnContent() {
        Page<Post> page = Mockito.mock(Page.class);
        Category category = new Category();
        category.setId(1L);

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createPostList());

        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));
        Mockito.when(postRepository.findByCategory(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(page);

        PagedResponse<Post> response = postService.getPostsByCategory(1L, 1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPostsByTag_whenTagNotFound_thenThrowException() {
        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.getPostsByTag(1L, 1, 2);
    }

    @Test
    public void getPostsByTag_whenNoPostsFound_thenReturnEmptyList() {
        Tag tag = new Tag();

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));
        Mockito.when(postRepository.findByTags(Mockito.any(), Mockito.any())).thenReturn(Page.empty());

        PagedResponse<Post> response = postService.getPostsByTag(1L, 1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getPostsByTag_whenPostsFound_thenReturnContent() {
        Page<Post> page = Mockito.mock(Page.class);
        Tag tag = new Tag();

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createPostList());

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));
        Mockito.when(postRepository.findByTags(Mockito.any(), Mockito.any())).thenReturn(page);

        PagedResponse<Post> response = postService.getPostsByTag(1L, 1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updatePost_whenPostNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.updatePost(1L, new PostRequest(), fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updatePost_whenCategoryNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(1L);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.updatePost(1L, postRequest, fakeUserPrincipal);
    }

    @Test
    public void updatePost_whenUserAuthorized_thenSavePost() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(1L);
        User user = new User();
        user.setId(1L);
        Post post = new Post();
        post.setUser(user);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Category()));

        postService.updatePost(1L, postRequest, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updatePost_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            PostRequest postRequest = new PostRequest();
            postRequest.setCategoryId(1L);
            User user = new User();
            user.setId(2L);
            Post post = new Post();
            post.setUser(user);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));
            Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Category()));

            postService.updatePost(1L, postRequest, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertEquals(false, e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deletePost_whenPostNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.deletePost(1L, fakeUserPrincipal);
    }

    @Test
    public void deletePost_whenUserAuthorized_thenDeleteAndReturnSuccessResponse() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User user = new User();
        user.setId(1L);
        Post post = new Post();
        post.setUser(user);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));

        ApiResponse response = postService.deletePost(1L, fakeUserPrincipal);

        Assert.assertNotNull(response.getMessage());
        Assert.assertEquals(true, response.getSuccess());
    }

    @Test(expected = UnauthorizedException.class)
    public void deletePost_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User user = new User();
            user.setId(2L);
            Post post = new Post();
            post.setUser(user);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(post));

            postService.deletePost(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertEquals(false, e.getApiResponse().getSuccess());
            Assert.assertNotNull(e.getApiResponse().getMessage());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addPost_whenUserNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.addPost(new PostRequest(), fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addPost_whenCategoryNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(1L);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.addPost(postRequest, fakeUserPrincipal);
    }

    @Test
    public void addPost_whenAllFound_thenAddPost() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        PostRequest postRequest = new PostRequest();
        postRequest.setCategoryId(1L);
        Category category = new Category();
        category.setName("Test");
        List<Tag> tags = new ArrayList<>();
        Post post = new Post();
        post.setTitle("Test Post");
        post.setBody("This is a test.");
        post.setCategory(category);
        post.setTags(tags);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.when(categoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Category()));
        Mockito.when(postRepository.save(Mockito.any())).thenReturn(post);

        postService.addPost(postRequest, fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPost_whenPostNotFound_thenThrowException() {
        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        postService.getPost(1L);
    }

    @Test
    public void getPost_whenPostFound_thenReturnPost() {
        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));

        postService.getPost(1L);
    }

    private List<Post> createPostList() {
        List<Post> list = new ArrayList<>();
        Post post = new Post();
        list.add(post);
        return list;
    }
}
