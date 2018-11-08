package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.comment.Comment;
import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public PagedResponse<?> getAllComments(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return commentService.getAllComments(postId, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest commentRequest, @PathVariable(name = "postId") Long postId, @CurrentUser UserPrincipal currentUser){
        return commentService.addComment(commentRequest, postId, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable(name = "postId") Long postId, @PathVariable(name = "id") Long id){
        return commentService.getComment(postId, id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateComment(@PathVariable(name = "postId") Long postId, @PathVariable(name = "id") Long id, @Valid @RequestBody CommentRequest commentRequest, @CurrentUser UserPrincipal currentUser){
        return commentService.updateComment(postId, id, commentRequest, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteComment(@PathVariable(name = "postId") Long postId, @PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return commentService.deleteComment(postId, id, currentUser);
    }

}
