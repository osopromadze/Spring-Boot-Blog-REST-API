package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") // Loads application-test.properties
@Transactional
public class CommentControllerTest2 {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Ensure roles exist in the test database
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }
        if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddComment_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is a test comment body that is long enough to meet the minimum length requirement.");

        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testuser"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.body").value("This is a test comment body that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.id").exists());

    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAllComments_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Create multiple comments
        CommentRequest commentRequest1 = new CommentRequest();
        commentRequest1.setBody("This is the first test comment body that is long enough to meet the minimum length requirement.");

        CommentRequest commentRequest2 = new CommentRequest();
        commentRequest2.setBody("This is the second test comment body that is long enough to meet the minimum length requirement.");

        // Add first comment
        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Add second comment
        mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Retrieve all comments for the post
        mockMvc.perform(get("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("testuser"))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.content[0].body").value("This is the second test comment body that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.content[1].name").value("testuser"))
                .andExpect(jsonPath("$.content[1].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.content[1].body").value("This is the first test comment body that is long enough to meet the minimum length requirement."))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateComment_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Create a comment
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is the original test comment body that is long enough to meet the minimum length requirement.");

        // Add comment to get its ID
        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Extract the comment ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long commentId = objectMapper.readTree(responseContent).get("id").asLong();

        // Update the comment
        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setBody("This is the updated test comment body that is long enough to meet the minimum length requirement.");

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.name").value("testuser"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.body").value("This is the updated test comment body that is long enough to meet the minimum length requirement."));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteComment_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Create a comment
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is a test comment body that is long enough to meet the minimum length requirement.");

        // Add comment to get its ID
        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Extract the comment ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long commentId = objectMapper.readTree(responseContent).get("id").asLong();

        // Delete the comment
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You successfully deleted comment"));

        // Verify the comment is deleted by trying to retrieve it
        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", post.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddComment_ResourceNotFoundException() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Use a non-existent post ID
        Long nonExistentPostId = 999L;

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is a test comment body that is long enough to meet the minimum length requirement.");

        mockMvc.perform(post("/api/posts/{postId}/comments", nonExistentPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found with id: '" + nonExistentPostId + "'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteComment_ResourceNotFoundException() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Use a non-existent comment ID
        Long nonExistentCommentId = 999L;

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", post.getId(), nonExistentCommentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found with id: '" + nonExistentCommentId + "'"));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = "USER") 
    public void testUpdateComment_ResourceNotFoundException() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);
        
         // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Use a non-existent post ID
        Long nonExistentPostId = 999L;
        Long nonExistentCommentId = 999L;

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is an updated test comment body.");

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", nonExistentPostId, nonExistentCommentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found with id: '" + nonExistentPostId + "'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetComment_ResourceNotFoundException() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create a post
        Post post = new Post();
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");
        post.setUser(user);
        postRepository.save(post);

        // Use a non-existent comment ID
        Long nonExistentCommentId = 999L;

        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", post.getId(), nonExistentCommentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment not found with id: '" + nonExistentCommentId + "'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetComment_BadRequest() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create two different posts
        Post post1 = new Post();
        post1.setTitle("Test Post Title 1");
        post1.setBody("This is the first test post body.");
        post1.setUser(user);
        post1 = postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Test Post Title 2");
        post2.setBody("This is the second test post body.");
        post2.setUser(user);
        post2 = postRepository.save(post2);

        // Create a comment for post1
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("This is a test comment body that is long enough to meet the minimum length requirement.");

        // Add comment to post1 to get its ID
        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", post1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Extract the comment ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long commentId = objectMapper.readTree(responseContent).get("id").asLong();

        // Try to get the comment using post2's ID (comment doesn't belong to post2)
        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", post2.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Comment does not belong to post"));
    }
}
