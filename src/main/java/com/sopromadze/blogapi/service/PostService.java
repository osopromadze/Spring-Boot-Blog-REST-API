package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<Post> getAllPosts(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Post> posts = postRepository.findAll(pageable);

        if (posts.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        }

        return new PagedResponse<>(posts.getContent(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    public PagedResponse<Post> getPostsCreatedBy(String username, int page, int size){
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Post> posts = postRepository.findByCreatedBy(user.getId(), pageable);

        if(posts.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        }
        return new PagedResponse<>(posts.getContent(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    public ResponseEntity<?> updatePost(Long id, Post newPost, UserPrincipal currentUser){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (post.getUser().getId().equals(currentUser.getId())){
            post.setUpdatedBy(post.getUser().getId());
            post.setTitle(newPost.getTitle());
            post.setBody(newPost.getBody());
            Post updatedPost = postRepository.save(post);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        }
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            post.setUpdatedBy(currentUser.getId());
            post.setTitle(newPost.getTitle());
            post.setBody(newPost.getBody());
            Post updatedPost = postRepository.save(post);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to edit this post"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deletePost(Long id, UserPrincipal currentUser){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        if (post.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            postRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted post"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(true, "You don't have permission to delete this post"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> addPost(Post post, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", 1L));
        post.setUser(user);
        Post newPost =  postRepository.save(post);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getPost(Long id){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
