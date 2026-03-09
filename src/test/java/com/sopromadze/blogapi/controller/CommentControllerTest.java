package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CommentRequest commentRequest;
    private Comment comment;
    private UserPrincipal userPrincipal;
    private Post post;
    private User user;

    /**
     * Test setup method
     * Initializes common test data and mock configurations used across all test methods.
     * Creates sample UserPrincipal, User, Post, CommentRequest, and Comment objects
     * that will be used for mocking service responses and testing controller behavior.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
        objectMapper = new ObjectMapper();

        userPrincipal = new UserPrincipal(1L, "Venkatesh", "Poshanapelli", "venkat", "venky@example.com", "password", Collections.emptyList());

        user = new User();
        user.setId(1L);
        user.setFirstName("Venkatesh");
        user.setLastName("Poshanapelli");
        user.setUsername("venkat");
        user.setEmail("venky@example.com");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post Title");
        post.setBody("This is a test post body.");

        commentRequest = new CommentRequest();
        commentRequest.setBody("This is a test comment body that is long enough to meet the minimum length requirement.");

        comment = new Comment();
        comment.setId(1L);
        comment.setName("Venkatesh");
        comment.setEmail("venky@example.com");
        comment.setBody("This is a test comment body.");
        comment.setPost(post);
        comment.setUser(user);
    }

    /**
     * Test successful comment addition to a post
     * Verifies that a comment can be added to a specific post with valid data
     */
    @Test
    public void testAddComment_Success() throws Exception {
        when(commentService.addComment(any(CommentRequest.class), anyLong(), any(UserPrincipal.class))).thenReturn(comment);

        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Venkatesh"))
                .andExpect(jsonPath("$.email").value("venky@example.com"))
                .andExpect(jsonPath("$.body").value("This is a test comment body."));

        verify(commentService, times(1)).addComment(any(CommentRequest.class), anyLong(), any(UserPrincipal.class));
    }


    /**
     * Test comment addition with invalid data
     * Verifies that the system properly validates and rejects invalid comment data (too short body)
     */
    @Test
    public void testAddComment_InvalidData() throws Exception {
        CommentRequest invalidRequest = new CommentRequest();
        invalidRequest.setBody("B"); // Too short

        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any(CommentRequest.class), anyLong(), any(UserPrincipal.class));
    }

    /**
     * Test successful retrieval of all comments for a post
     * Verifies that all comments for a specific post can be retrieved with pagination
     */
    @Test
    public void testGetAllComments_Success() throws Exception {
        PagedResponse<Comment> pagedResponse = new PagedResponse<>(
                Arrays.asList(comment), 0, 10, 1L, 1, true);

        when(commentService.getAllComments(anyLong(), anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/posts/{postId}/comments", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Venkatesh"))
                .andExpect(jsonPath("$.content[0].email").value("venky@example.com"))
                .andExpect(jsonPath("$.content[0].body").value("This is a test comment body."));

        verify(commentService, times(1)).getAllComments(anyLong(), anyInt(), anyInt());
    }

    /**
     * Test successful retrieval of a specific comment
     * Verifies that a comment can be retrieved by its ID with correct data
     */
    @Test
    public void testGetComment_Success() throws Exception {
        when(commentService.getComment(anyLong(), anyLong())).thenReturn(comment);

        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Venkatesh"))
                .andExpect(jsonPath("$.email").value("venky@example.com"))
                .andExpect(jsonPath("$.body").value("This is a test comment body."));

        verify(commentService, times(1)).getComment(anyLong(), anyLong());
    }

    /**
     * Test retrieval of non-existent comment
     * Verifies that the system returns 404 when trying to retrieve a comment that doesn't exist
     */
    @Test
    public void testGetComment_NonExistentComment() throws Exception {
        when(commentService.getComment(anyLong(), anyLong())).thenThrow(new ResourceNotFoundException("Comment", "id", 999L));

        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", 1L, 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(commentService, times(1)).getComment(anyLong(), anyLong());
    }

    /**
     * Test successful comment update
     * Verifies that an existing comment can be updated with new data
     */
    @Test
    public void testUpdateComment_Success() throws Exception {
        CommentRequest updateRequest = new CommentRequest();
        updateRequest.setBody("This is an updated comment body that is long enough to meet the minimum length requirement.");

        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setName("Venkatesh");
        updatedComment.setEmail("venky@example.com");
        updatedComment.setBody("This is an updated comment body.");

        when(commentService.updateComment(anyLong(), anyLong(), any(CommentRequest.class), any(UserPrincipal.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Venkatesh"))
                .andExpect(jsonPath("$.email").value("venky@example.com"))
                .andExpect(jsonPath("$.body").value("This is an updated comment body."));

        verify(commentService, times(1)).updateComment(anyLong(), anyLong(), any(CommentRequest.class), any(UserPrincipal.class));
    }


    /**
     * Test comment update with invalid data
     * Verifies that the system properly validates and rejects invalid update data (too short body)
     */
    @Test
    public void testUpdateComment_InvalidData() throws Exception {
        CommentRequest invalidRequest = new CommentRequest();
        invalidRequest.setBody("B"); // Too short

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(anyLong(), anyLong(), any(CommentRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test successful comment deletion
     * Verifies that a comment can be deleted and returns a success response
     */
    @Test
    public void testDeleteComment_Success() throws Exception {
        ApiResponse apiResponse = new ApiResponse(true, "Comment deleted successfully");

        when(commentService.deleteComment(anyLong(), anyLong(), any(UserPrincipal.class))).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));

        verify(commentService, times(1)).deleteComment(anyLong(), anyLong(), any(UserPrincipal.class));
    }


    /**
     * Test deletion of non-existent comment
     * Verifies that the system returns 404 when trying to delete a comment that doesn't exist
     */
    @Test
    public void testDeleteComment_NonExistentComment() throws Exception {
        when(commentService.deleteComment(anyLong(), anyLong(), any(UserPrincipal.class)))
                .thenThrow(new ResourceNotFoundException("Comment", "id", 999L));

        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isNotFound());

        verify(commentService, times(1)).deleteComment(anyLong(), anyLong(), any(UserPrincipal.class));
    }
}