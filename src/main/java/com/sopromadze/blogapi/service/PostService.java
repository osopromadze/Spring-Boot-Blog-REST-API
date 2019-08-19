package com.sopromadze.blogapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.category.Category;
import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.tag.Tag;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.util.AppConstants;
import com.sopromadze.blogapi.util.AppUtils;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;
    
    private final CategoryRepository categoryRepository;
    
    private final TagRepository tagRepository;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
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

    public PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size){
        validatePageNumberAndSize(page, size);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Post> posts = postRepository.findByCreatedBy(user.getId(), pageable);

        if(posts.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        }
        return new PagedResponse<>(posts.getContent(), posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }
    
    public PagedResponse<Post> getPostsByCategory(Long id, int page, int size){
    	AppUtils.validatePageNumberAndSize(page, size);
    	Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    	Page<Post> posts = postRepository.findByCategory(category.getId(), pageable);
    	
    	List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();
    	
    	return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }
    
    public PagedResponse<Post> getPostsByTag(Long id, int page, int size){
    	AppUtils.validatePageNumberAndSize(page, size);
    	
    	Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");
    	
    	Page<Post> posts = postRepository.findByTags(Arrays.asList(tag), pageable);
    	
    	List<Post> content = posts.getNumberOfElements() == 0 ? Collections.emptyList() : posts.getContent();
    	
    	return new PagedResponse<>(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    public ResponseEntity<?> updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        Category category = categoryRepository.findById(newPostRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", newPostRequest.getCategoryId()));
        if (post.getUser().getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            post.setTitle(newPostRequest.getTitle());
            post.setBody(newPostRequest.getBody());
            post.setCategory(category);
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

    public ResponseEntity<?> addPost(PostRequest postRequest, UserPrincipal currentUser){
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", 1L));
        Category category = categoryRepository.findById(postRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category", "id", postRequest.getCategoryId()));
        
        List<Tag> tags = new ArrayList<Tag>();
        
        for(String name : postRequest.getTags()) {
        	Tag tag = tagRepository.findByName(name);
        	tag = tag == null ? tagRepository.save(new Tag(name)) : tag;
        	
        	tags.add(tag);
        }
        
        Post post = new Post();
        post.setBody(postRequest.getBody());
        post.setTitle(postRequest.getTitle());
        post.setCategory(category);
        post.setUser(user);
        post.setTags(tags);
        
        Post newPost =  postRepository.save(post);
        
        PostResponse postResponse = new PostResponse();
        
        postResponse.setTitle(newPost.getTitle());
        postResponse.setBody(newPost.getBody());
        postResponse.setCategory(newPost.getCategory().getName());
        
        List<String> tagNames = new ArrayList<String>();
        
        for(Tag tag : newPost.getTags()) {
        	tagNames.add(tag.getName());
        }
        
        postResponse.setTags(tagNames);
        
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
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
