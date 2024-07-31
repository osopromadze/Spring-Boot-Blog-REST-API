package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.validation.Valid;

@Tag(name = "3- Posts", description = "Operations related to  blog posts")

@RestController
@RequestMapping("/api/posts")
public class PostController {
	@Autowired
	private PostService postService;

	@Operation(description = "Get all posts", summary = "Get all posts")
	@GetMapping
	public ResponseEntity<PagedResponse<Post>> getAllPosts(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		PagedResponse<Post> response = postService.getAllPosts(page, size);

		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@Operation(description = "Get post by category id", summary = "Get posts by category", hidden = true)
	@GetMapping("/category/{id}")
	public ResponseEntity<PagedResponse<Post>> getPostsByCategory(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
			@PathVariable(name = "id") Long id) {
		PagedResponse<Post> response = postService.getPostsByCategory(id, page, size);

		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@Operation(description = "Get all posts by tag", summary = "Get all posts by tag", hidden = true)
	@GetMapping("/tag/{id}")
	public ResponseEntity<PagedResponse<Post>> getPostsByTag(
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
			@PathVariable(name = "id") Long id) {
		PagedResponse<Post> response = postService.getPostsByTag(id, page, size);

		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@Operation(description = "Create new post (By logged in user)", summary = "Create post")
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<PostResponse> addPost(@Valid @RequestBody PostRequest postRequest,
			@CurrentUser UserPrincipal currentUser) {
		PostResponse postResponse = postService.addPost(postRequest, currentUser);

		return new ResponseEntity< >(postResponse, HttpStatus.CREATED);
	}

	@Operation(description = "Get post by id", summary = "Get post")
	@GetMapping("/{id}")
	public ResponseEntity<Post> getPost(@PathVariable(name = "id") Long id) {
		Post post = postService.getPost(id);

		return new ResponseEntity< >(post, HttpStatus.OK);
	}

	@Operation(description = "Update post (If post belongs to logged in user or logged in user is admin)", summary = "Update post")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<Post> updatePost(@PathVariable(name = "id") Long id,
			@Valid @RequestBody PostRequest newPostRequest, @CurrentUser UserPrincipal currentUser) {
		Post post = postService.updatePost(id, newPostRequest, currentUser);

		return new ResponseEntity< >(post, HttpStatus.OK);
	}

	@Operation(description = "Delete post (If post belongs to logged in user or logged in user is admin)", summary = "Delete post")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deletePost(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		ApiResponse apiResponse = postService.deletePost(id, currentUser);

		return new ResponseEntity< >(apiResponse, HttpStatus.OK);
	}
}
