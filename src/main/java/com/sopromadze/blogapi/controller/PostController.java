package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public PagedResponse<Post> getAllPosts(
                @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return postService.getAllPosts(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPost(@Valid @RequestBody Post post, @CurrentUser UserPrincipal currentUser){
        return postService.addPost(post, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable(name = "id") Long id){
        return postService.getPost(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePost(@PathVariable(name = "id") Long id, @Valid @RequestBody Post newPost, @CurrentUser UserPrincipal currentUser){
        return postService.updatePost(id, newPost, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return postService.deletePost(id, currentUser);
    }
}
