package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class CommentServiceImplTests {

    @Mock
    private AppUtils appUtils;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test(expected = ResourceNotFoundException.class)
    public void addComment_whenNoResourceFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        commentService.addComment(new CommentRequest(), 1L, fakeUserPrincipal);
    }

    @Test
    public void addComment_whenPostFound_thenSaveComment() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Post testPost = new Post();
        User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(fakeUser);

        commentService.addComment(new CommentRequest(), 1L, fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getComment_whenPostNotFound_thenThrowException() {
        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        commentService.getComment(1L, 1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getComment_whenCommentNotFound_thenThrowException() {
        Post fakePost = new Post();

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(fakePost));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        commentService.getComment(1L, 1L);
    }

    @Test
    public void getComment_whenCommentFoundAndPostIdMatches_theReturnComment() {
        Post testPost = new Post();
        testPost.setId(1L);
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setPost(testPost);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

        commentService.getComment(1L, 1L);
    }

    @Test(expected = BlogapiException.class)
    public void getComment_whenPostIdNotMatch_thenThrowException() {
        try {
            Post testPost = new Post();
            testPost.setId(1L);
            Post fakePost = new Post();
            fakePost.setId(2L);
            Comment testComment = new Comment();
            testComment.setId(1L);
            testComment.setPost(fakePost);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
            Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

            commentService.getComment(1L, 1L);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateComment_whenPostNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        commentService.updateComment(1L, 1L, new CommentRequest(), fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateComment_whenCommentNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        commentService.updateComment(1L, 1L, new CommentRequest(), fakeUserPrincipal);
    }

    @Test(expected = BlogapiException.class)
    public void updateComment_whenPostIdNotMatch_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Post testPost = new Post();
            testPost.setId(1L);
            Post fakePost = new Post();
            fakePost.setId(2L);
            Comment testComment = new Comment();
            testComment.setId(1L);
            testComment.setPost(fakePost);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
            Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

            commentService.updateComment(1L, 1L, new CommentRequest(), fakeUserPrincipal);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void updateComment_whenCommentFoundAndUserAuthorized_thenSaveComment() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Post testPost = new Post();
        testPost.setId(1L);
        User testUser = new User();
        testUser.setId(1L);
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setPost(testPost);
        testComment.setUser(testUser);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

        commentService.updateComment(1L, 1L, new CommentRequest(), fakeUserPrincipal);
    }

    @Test(expected = BlogapiException.class)
    public void updateComment_whenUserUnauthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Post testPost = new Post();
            testPost.setId(1L);
            User testUser = new User();
            testUser.setId(2L);
            Comment testComment = new Comment();
            testComment.setId(1L);
            testComment.setPost(testPost);
            testComment.setUser(testUser);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
            Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

            commentService.updateComment(1L, 1L, new CommentRequest(), fakeUserPrincipal);
        } catch (BlogapiException e) {
            Assert.assertEquals(401, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteComment_whenPostNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        commentService.deleteComment(1L, 1L, fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteComment_whenCommentNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        commentService.deleteComment(1L, 1L, fakeUserPrincipal);
    }

    @Test
    public void deleteComment_whenCommentNotOnPost_thenSendResponseWithInformation() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Post testPost = new Post();
        testPost.setId(1L);
        Post fakePost = new Post();
        fakePost.setId(2L);
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setPost(fakePost);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

        ApiResponse response = commentService.deleteComment(1L, 1L, fakeUserPrincipal);
        Assert.assertEquals(false, response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test
    public void deleteComment_whenUserAuthorized_thenDeleteComment() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Post testPost = new Post();
        testPost.setId(1L);
        User testUser = new User();
        testUser.setId(1L);
        Comment testComment = new Comment();
        testComment.setId(1L);
        testComment.setPost(testPost);
        testComment.setUser(testUser);

        Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
        Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

        ApiResponse response = commentService.deleteComment(1L, 1L, fakeUserPrincipal);
        Assert.assertEquals(true, response.getSuccess());
        Assert.assertNotNull(response.getMessage());
    }

    @Test(expected = BlogapiException.class)
    public void deleteComment_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Post testPost = new Post();
            testPost.setId(1L);
            User testUser = new User();
            testUser.setId(2L);
            Comment testComment = new Comment();
            testComment.setId(1L);
            testComment.setPost(testPost);
            testComment.setUser(testUser);

            Mockito.when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testPost));
            Mockito.when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testComment));

            commentService.deleteComment(1L, 1L, fakeUserPrincipal);
        } catch (BlogapiException e) {
            Assert.assertEquals(401, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }
}
