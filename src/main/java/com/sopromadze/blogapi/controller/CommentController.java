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

import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import com.sopromadze.blogapi.util.AppConstants;

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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateComment(@PathVariable(name = "postId") Long postId, @PathVariable(name = "id") Long id, @Valid @RequestBody CommentRequest commentRequest, @CurrentUser UserPrincipal currentUser){
        return commentService.updateComment(postId, id, commentRequest, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable(name = "postId") Long postId, @PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return commentService.deleteComment(postId, id, currentUser);
    }

}
