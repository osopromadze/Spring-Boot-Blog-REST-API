package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(SpringRunner.class)
public class CommentControllerTests {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private UserPrincipal userPrincipal;

    @Before
    public void setup() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
    }

    @Test
    public void deleteComment_whenCommentServiceUnsuccessful_thenSend400Status() {
        ApiResponse serviceResponse = new ApiResponse();
        serviceResponse.setSuccess(false);

        Mockito.when(commentService.deleteComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse> response = commentController.deleteComment(1L,1L, userPrincipal);
        Assert.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void deleteComment_whenCommentServiceSuccessful_thenSend200Status() {
        ApiResponse serviceResponse = new ApiResponse();
        serviceResponse.setSuccess(true);

        Mockito.when(commentService.deleteComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(serviceResponse);

        ResponseEntity<ApiResponse> response = commentController.deleteComment(1L,1L, userPrincipal);
        Assert.assertEquals(200, response.getStatusCodeValue());
    }
}
