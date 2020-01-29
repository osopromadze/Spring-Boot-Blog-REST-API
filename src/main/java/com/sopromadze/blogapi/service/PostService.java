package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface PostService {

	PagedResponse<Post> getAllPosts(int page, int size);

	PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size);

	PagedResponse<Post> getPostsByCategory(Long id, int page, int size);

	PagedResponse<Post> getPostsByTag(Long id, int page, int size);

	ResponseEntity<?> updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser);

	ResponseEntity<?> deletePost(Long id, UserPrincipal currentUser);

	ResponseEntity<?> addPost(PostRequest postRequest, UserPrincipal currentUser);

	ResponseEntity<?> getPost(Long id);

}