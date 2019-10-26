package com.sopromadze.blogapi.service;

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
import com.sopromadze.blogapi.model.comment.Comment;
import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.util.AppConstants;

@Service
public class CommentService {
	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	public PagedResponse<?> getAllComments(Long postId, int page, int size) {
		validatePageNumberAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

		return new PagedResponse<>(comments.getContent(), comments.getNumber(), comments.getSize(),
				comments.getTotalElements(), comments.getTotalPages(), comments.isLast());
	}

	public ResponseEntity<?> addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Comment comment = new Comment(commentRequest.getBody());
		comment.setUser(user);
		comment.setPost(post);
		comment.setName(currentUser.getUsername());
		comment.setEmail(currentUser.getEmail());
		Comment newComment = commentRepository.save(comment);
		return new ResponseEntity<>(newComment, HttpStatus.CREATED);
	}

	public ResponseEntity<?> getComment(Long postId, Long id) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
		if (comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(comment, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "Comment does not belong to post"), HttpStatus.BAD_REQUEST);
	}

	public ResponseEntity<?> updateComment(Long postId, Long id, CommentRequest commentRequest,
			UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

		if (!comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(new ApiResponse(false, "Comment does not belong to post"),
					HttpStatus.BAD_REQUEST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			comment.setBody(commentRequest.getBody());
			Comment updatedComment = commentRepository.save(comment);
			return new ResponseEntity<>(updatedComment, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update this comment"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> deleteComment(Long postId, Long id, UserPrincipal currentUser) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

		if (!comment.getPost().getId().equals(post.getId())) {
			return new ResponseEntity<>(new ApiResponse(false, "Comment does not belong to post"),
					HttpStatus.BAD_REQUEST);
		}

		if (comment.getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			commentRepository.deleteById(comment.getId());
			return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted comment"), HttpStatus.OK);
		}

		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to delete this comment"),
				HttpStatus.BAD_REQUEST);
	}

	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BadRequestException("Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
}
