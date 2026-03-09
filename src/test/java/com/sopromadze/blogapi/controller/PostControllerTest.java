package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.model.Post;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PostRequest postRequest;
    private PostResponse postResponse;
    private UserPrincipal userPrincipal;
    private Category category;
    private List<Tag> tags;

    /**
     * Test setup method
     * Initializes common test data and mock configurations used across all test methods.
     * Creates sample Category, Tag, PostRequest, PostResponse, and UserPrincipal objects
     * that will be used for mocking service responses and testing controller behavior.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();

        userPrincipal = new UserPrincipal(1L, "Venkatesh", "Poshanapelli", "venkat", "venky@example.com", "password", Collections.emptyList());

        category = new Category();
        category.setId(1L);
        category.setName("Technology");

        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Java");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Spring");

        tags = Arrays.asList(tag1, tag2);

        postRequest = new PostRequest();
        postRequest.setTitle("Test Post Title That Is Long Enough");
        postRequest.setBody("This is a test post body that is long enough to meet the minimum length requirement.");
        postRequest.setCategoryId(1L);
        postRequest.setTags(Arrays.asList("Java", "Spring"));

        postResponse = new PostResponse();
        postResponse.setTitle("Test Post");
        postResponse.setBody("This is a test post body.");
        postResponse.setCategory("Technology");
        postResponse.setTags(Arrays.asList("Java", "Spring"));
    }

    /**
     * Test successful post addition
     * Verifies that a post can be added with valid data and returns the created post
     */
    @Test
    public void testAddPost_Success() throws Exception {
        when(postService.addPost(any(PostRequest.class), any(UserPrincipal.class))).thenReturn(postResponse);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.body").value("This is a test post body."))
                .andExpect(jsonPath("$.category").value("Technology"))
                .andExpect(jsonPath("$.tags[0]").value("Java"))
                .andExpect(jsonPath("$.tags[1]").value("Spring"));

        verify(postService, times(1)).addPost(any(PostRequest.class), any(UserPrincipal.class));
    }


    /**
     * Test post addition with invalid data
     * Verifies that the system properly validates and rejects invalid post data (too short title/body)
     */
    @Test
    public void testAddPost_InvalidData() throws Exception {
        PostRequest invalidRequest = new PostRequest(); 
        invalidRequest.setTitle("T"); // Too short
        invalidRequest.setBody("B"); // Too short
        invalidRequest.setCategoryId(1L);
        invalidRequest.setTags(Arrays.asList("Java"));

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isBadRequest());

        verify(postService, never()).addPost(any(PostRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test post addition with missing required fields
     * Verifies that the system properly validates and rejects incomplete post data
     */
    @Test
    public void testAddPost_MissingRequiredFields() throws Exception {
        PostRequest incompleteRequest = new PostRequest();
        // Missing title, body, categoryId

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isBadRequest());

        verify(postService, never()).addPost(any(PostRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test successful post update
     * Verifies that an existing post can be updated with new data
     */
    @Test
    public void testUpdatePost_Success() throws Exception {
        Long postId = 1L;
        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("Updated Post Title That Is Long Enough");
        updateRequest.setBody("This is an updated post body that is long enough to meet the minimum length requirement.");
        updateRequest.setCategoryId(1L);
        updateRequest.setTags(Arrays.asList("Java", "Spring", "Updated"));

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setTitle("Updated Post Title That Is Long Enough");
        updatedPost.setBody("This is an updated post body that is long enough to meet the minimum length requirement.");

        when(postService.updatePost(anyLong(), any(PostRequest.class), any(UserPrincipal.class))).thenReturn(updatedPost);

        mockMvc.perform(put("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post Title That Is Long Enough"))
                .andExpect(jsonPath("$.body").value("This is an updated post body that is long enough to meet the minimum length requirement."));

        verify(postService, times(1)).updatePost(anyLong(), any(PostRequest.class), any(UserPrincipal.class));
    }


    /**
     * Test post update with invalid data
     * Verifies that the system properly validates and rejects invalid update data (too short title/body)
     */
    @Test
    public void testUpdatePost_InvalidData() throws Exception {
        Long postId = 1L;
        PostRequest invalidRequest = new PostRequest();
        invalidRequest.setTitle("T"); // Too short
        invalidRequest.setBody("B"); // Too short
        invalidRequest.setCategoryId(1L);
        invalidRequest.setTags(Arrays.asList("Java"));

        mockMvc.perform(put("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isBadRequest());

        verify(postService, never()).updatePost(anyLong(), any(PostRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test successful retrieval of a specific post
     * Verifies that a post can be retrieved by its ID with correct data
     */
    @Test
    public void testGetPost_Success() throws Exception {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");

        when(postService.getPost(anyLong())).thenReturn(post);

        mockMvc.perform(get("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Test Post Title"))
                .andExpect(jsonPath("$.body").value("This is a test post body."));

        verify(postService, times(1)).getPost(anyLong());
    }

    /**
     * Test retrieval of non-existent post
     * Verifies that the system returns 404 when trying to retrieve a post that doesn't exist
     */
    @Test
    public void testGetPost_NonExistentPost() throws Exception {
        Long postId = 999L;

        when(postService.getPost(anyLong())).thenThrow(new ResourceNotFoundException("Post", "id", postId));

        mockMvc.perform(get("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(postService, times(1)).getPost(anyLong());
    }

    /**
     * Test successful post deletion
     * Verifies that a post can be deleted and returns a success response
     */
    @Test
    public void testDeletePost_Success() throws Exception {
        Long postId = 1L;
        ApiResponse apiResponse = new ApiResponse(true, "Post deleted successfully");

        when(postService.deletePost(anyLong(), any(UserPrincipal.class))).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post deleted successfully"));

        verify(postService, times(1)).deletePost(anyLong(), any(UserPrincipal.class));
    }


    /**
     * Test deletion of non-existent post
     * Verifies that the system returns 404 when trying to delete a post that doesn't exist
     */
    @Test
    public void testDeletePost_NonExistentPost() throws Exception {
        Long postId = 999L;

        when(postService.deletePost(anyLong(), any(UserPrincipal.class)))
                .thenThrow(new ResourceNotFoundException("Post", "id", postId));

        mockMvc.perform(delete("/api/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isNotFound());

        verify(postService, times(1)).deletePost(anyLong(), any(UserPrincipal.class));
    }

    /**
     * Test successful retrieval of all posts
     * Verifies that all posts can be retrieved with pagination support
     */
    @Test
    public void testGetAllPosts_Success() throws Exception {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("First Post Title");
        post1.setBody("This is the first post body.");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post Title");
        post2.setBody("This is the second post body.");

        List<Post> posts = Arrays.asList(post1, post2);

        PagedResponse<Post> pagedResponse = 
        new PagedResponse<>(posts, 1, 10, 2, 1, true);

        when(postService.getAllPosts(anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/posts")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("First Post Title"))
                .andExpect(jsonPath("$.content[0].body").value("This is the first post body."))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].title").value("Second Post Title"))
                .andExpect(jsonPath("$.content[1].body").value("This is the second post body."))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        verify(postService, times(1)).getAllPosts(1, 10);
    }

    /**
     * Test successful retrieval of posts by tag
     * Verifies that posts can be retrieved by tag ID with pagination support
     */
    @Test
    public void testGetPostsByTag_Success() throws Exception {
        Long tagId = 1L;
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("Java Post Title");
        post1.setBody("This is a Java-related post body.");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Spring Post Title");
        post2.setBody("This is a Spring-related post body.");

        List<Post> posts = Arrays.asList(post1, post2);

        PagedResponse<Post> pagedResponse = 
            new PagedResponse<>(posts, 0, 10, 2, 1, true);

        when(postService.getPostsByTag(anyLong(), anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/posts/tag/{id}", tagId)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Java Post Title"))
                .andExpect(jsonPath("$.content[0].body").value("This is a Java-related post body."))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].title").value("Spring Post Title"))
                .andExpect(jsonPath("$.content[1].body").value("This is a Spring-related post body."))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        verify(postService, times(1)).getPostsByTag(tagId, 0, 10);
    }
}
