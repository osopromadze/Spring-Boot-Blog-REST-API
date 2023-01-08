package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.payload.response.ApiResponse;
import com.sopromadze.blogapi.payload.response.PagedResponse;
import com.sopromadze.blogapi.payload.request.PostRequest;
import com.sopromadze.blogapi.payload.response.PostResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface PostService {

	PagedResponse<Post> getAllPosts(int page, int size);

	PagedResponse<Post> getPostsByCreatedBy(String username, int page, int size);

	PagedResponse<Post> getPostsByCategory(Long id, int page, int size);

	PagedResponse<Post> getPostsByTag(Long id, int page, int size);

	Post updatePost(Long id, PostRequest newPostRequest, UserPrincipal currentUser);

	ApiResponse deletePost(Long id, UserPrincipal currentUser);

	PostResponse addPost(PostRequest postRequest, UserPrincipal currentUser);

	Post getPost(Long id);

}
