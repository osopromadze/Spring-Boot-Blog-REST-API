package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.PageRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostControllerTest2 {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private Category testCategory;
    private List<Tag> testTags;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Ensure roles exist
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }

        // Create test user
        testUser = new User("John", "Doe", "johndoe", "john.doe@example.com", "password123");
        userRepository.save(testUser);

        // Create test category
        testCategory = new Category();
        testCategory.setName("Technology");
        categoryRepository.save(testCategory);

        // Create test tags
        Tag tag1 = new Tag();
        tag1.setName("Java");
        tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Spring");
        tagRepository.save(tag2);

        testTags = Arrays.asList(tag1, tag2);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testAddPost_Success() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Test Post Title That Is Long Enough");
        postRequest.setBody("This is a test post body that is long enough to meet the minimum length requirement.");
        postRequest.setCategoryId(testCategory.getId());
        postRequest.setTags(Arrays.asList("Java", "Spring"));

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Post Title That Is Long Enough"))
                .andExpect(jsonPath("$.body").value("This is a test post body that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.category").value("Technology"))
                .andExpect(jsonPath("$.tags[0]").value("Java"))
                .andExpect(jsonPath("$.tags[1]").value("Spring"));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testAddPost_MissingRequiredFields() throws Exception {
        PostRequest postRequest = new PostRequest();
        // Missing title, body, categoryId

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testAddPost_EmptyTags() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Test Post Title That Is Long Enough");
        postRequest.setBody("This is a test post body that is long enough to meet the minimum length requirement.");
        postRequest.setCategoryId(testCategory.getId());
        postRequest.setTags(Collections.emptyList());

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Post Title That Is Long Enough"))
                .andExpect(jsonPath("$.body").value("This is a test post body that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.category").value("Technology"))
                .andExpect(jsonPath("$.tags").isEmpty());
    }

    @Test
    public void testAddPost_Unauthenticated() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle("Test Post Title That Is Long Enough");
        postRequest.setBody("This is a test post body that is long enough to meet the minimum length requirement.");
        postRequest.setCategoryId(testCategory.getId());
        postRequest.setTags(Arrays.asList("Java"));

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testUpdatePost_Success() throws Exception {
        // Create a post directly in the database for testing
        Post post = new Post();
        post.setTitle("Original Post Title");
        post.setBody("Original post body content that is long enough to meet the minimum length requirement.");
        post.setCategory(testCategory);
        post.setUser(testUser);
        post = postRepository.save(post);

        // Now update the post via API
        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("Updated Post Title");
        updateRequest.setBody("Updated post body content that is long enough to meet the minimum length requirement and has more characters.");
        updateRequest.setCategoryId(testCategory.getId());
        updateRequest.setTags(Arrays.asList("Spring", "Hibernate"));

        mockMvc.perform(put("/api/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post Title"))
                .andExpect(jsonPath("$.body").value("Updated post body content that is long enough to meet the minimum length requirement and has more characters."))
                .andExpect(jsonPath("$.category.name").value("Technology"));
    }
    @Test
    public void testUpdatePost_Unauthenticated() throws Exception {
        // Create a post directly in the database for testing
        Post post = new Post();
        post.setTitle("Original Post Title");
        post.setBody("Original post body content that is long enough to meet the minimum length requirement.");
        post.setCategory(testCategory);
        post.setUser(testUser);
        post = postRepository.save(post);

        // Now attempt to update the post via API without authentication
        PostRequest updateRequest = new PostRequest();
        updateRequest.setTitle("Updated Post Title");
        updateRequest.setBody("Updated post body content that is long enough to meet the minimum length requirement and has more characters.");
        updateRequest.setCategoryId(testCategory.getId());
        updateRequest.setTags(Arrays.asList("Spring", "Hibernate"));

        mockMvc.perform(put("/api/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testGetAllPosts_Success() throws Exception {
        // Create test posts in the database
        Post post1 = new Post();
        post1.setTitle("First Test Post");
        post1.setBody("This is the body of the first test post that is long enough to meet the minimum length requirement.");
        post1.setCategory(testCategory);
        post1.setUser(testUser);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Second Test Post");
        post2.setBody("This is the body of the second test post that is long enough to meet the minimum length requirement.");
        post2.setCategory(testCategory);
        post2.setUser(testUser);
        postRepository.save(post2);

        // Test getAllPosts endpoint
        mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Second Test Post"))
                .andExpect(jsonPath("$.content[0].body").value("This is the body of the second test post that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.content[1].title").value("First Test Post"))
                .andExpect(jsonPath("$.content[1].body").value("This is the body of the first test post that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testDeletePost_Success() throws Exception {
        // Create a post directly in the database for testing
        Post post = new Post();
        post.setTitle("Post to Delete");
        post.setBody("This is a post that will be deleted for testing purposes.");
        post.setCategory(testCategory);
        post.setUser(testUser);
        post = postRepository.save(post);

        // Verify the post exists before deletion
        mockMvc.perform(get("/api/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Post to Delete"));

        // Delete the post
        mockMvc.perform(delete("/api/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You successfully deleted post"));

        // Verify the post no longer exists
        mockMvc.perform(get("/api/posts/{id}", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testGetPostsByTag_Success() throws Exception {
        // Create test posts with tags in the database
        Post post1 = new Post();
        post1.setTitle("Java Programming Post");
        post1.setBody("This is a post about Java programming that is long enough to meet the minimum length requirement.");
        post1.setCategory(testCategory);
        post1.setUser(testUser);
        post1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Spring Framework Post");
        post2.setBody("This is a post about Spring framework that is long enough to meet the minimum length requirement.");
        post2.setCategory(testCategory);
        post2.setUser(testUser);
        post2 = postRepository.save(post2);

        // Flush changes to the database
        entityManager.flush();
        
        // Associate posts with tags using the junction table via EntityManager
        // Get fresh instances from the persistence context
        post1 = entityManager.find(Post.class, post1.getId());
        post2 = entityManager.find(Post.class, post2.getId());
        
        // Use reflection to set the tags directly on the internal field to bypass the unmodifiable list wrapper
        try {
            java.lang.reflect.Field tagsField = Post.class.getDeclaredField("tags");
            tagsField.setAccessible(true);
            
            List<Tag> post1Tags = new ArrayList<>();
            post1Tags.add(testTags.get(0)); // Java tag
            tagsField.set(post1, post1Tags);
            
            List<Tag> post2Tags = new ArrayList<>();
            post2Tags.add(testTags.get(1)); // Spring tag
            tagsField.set(post2, post2Tags);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        
        postRepository.save(post1);
        postRepository.save(post2);
        entityManager.flush();

        // Test getPostsByTag endpoint for Java tag
        mockMvc.perform(get("/api/posts/tag/{id}", testTags.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Java Programming Post"))
                .andExpect(jsonPath("$.content[0].body").value("This is a post about Java programming that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testGetPostsByCategory_Success() throws Exception {
        // Create test posts in the same category
        Post post1 = new Post();
        post1.setTitle("Technology Post 1");
        post1.setBody("This is the first technology post that is long enough to meet the minimum length requirement.");
        post1.setCategory(testCategory);
        post1.setUser(testUser);
        post1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Technology Post 2");
        post2.setBody("This is the second technology post that is long enough to meet the minimum length requirement.");
        post2.setCategory(testCategory);
        post2.setUser(testUser);
        post2 = postRepository.save(post2);

        // Create a post in a different category to ensure it's not included
        Category differentCategory = new Category();
        differentCategory.setName("Science");
        categoryRepository.save(differentCategory);

        Post post3 = new Post();
        post3.setTitle("Science Post");
        post3.setBody("This is a science post that is long enough to meet the minimum length requirement.");
        post3.setCategory(differentCategory);
        post3.setUser(testUser);
        post3 = postRepository.save(post3);

        entityManager.flush();

        // Test getPostsByCategory endpoint for Technology category with pagination parameters
        mockMvc.perform(get("/api/posts/category/{id}", testCategory.getId())
                .param("page", "0")
                .param("size", "30")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Technology Post 2"))
                .andExpect(jsonPath("$.content[0].body").value("This is the second technology post that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.content[1].title").value("Technology Post 1"))
                .andExpect(jsonPath("$.content[1].body").value("This is the first technology post that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testGetPostsByCreatedBy_Success() throws Exception {
        // Create test posts for the current user
        Post post1 = new Post();
        post1.setTitle("User's First Post");
        post1.setBody("This is the first post by the user that is long enough to meet the minimum length requirement.");
        post1.setCategory(testCategory);
        post1.setUser(testUser);
        post1.setCreatedBy(testUser.getId()); // Set createdBy manually for testing
        post1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("User's Second Post");
        post2.setBody("This is the second post by the user that is long enough to meet the minimum length requirement.");
        post2.setCategory(testCategory);
        post2.setUser(testUser);
        post2.setCreatedBy(testUser.getId()); // Set createdBy manually for testing
        post2 = postRepository.save(post2);

        // Create a post for a different user to ensure it's not included
        User otherUser = new User("Jane", "Smith", "janesmith", "jane.smith@example.com", "password123");
        userRepository.save(otherUser);

        Post post3 = new Post();
        post3.setTitle("Other User's Post");
        post3.setBody("This is a post by another user that is long enough to meet the minimum length requirement.");
        post3.setCategory(testCategory);
        post3.setUser(otherUser);
        post3 = postRepository.save(post3);

        entityManager.flush();

        // Test getPostsByCreatedBy endpoint for the current user with pagination parameters
        mockMvc.perform(get("/api/users/{username}/posts", testUser.getUsername())
                .param("page", "0")
                .param("size", "30")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("User's Second Post"))
                .andExpect(jsonPath("$.content[0].body").value("This is the second post by the user that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.content[1].title").value("User's First Post"))
                .andExpect(jsonPath("$.content[1].body").value("This is the first post by the user that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }
}
