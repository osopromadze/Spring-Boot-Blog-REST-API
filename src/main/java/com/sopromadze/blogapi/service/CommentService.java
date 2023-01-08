package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.post.Comment;
import com.sopromadze.blogapi.payload.response.ApiResponse;
import com.sopromadze.blogapi.payload.request.CommentRequest;
import com.sopromadze.blogapi.payload.response.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface CommentService {

	PagedResponse<Comment> getAllComments(Long postId, int page, int size);

	Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser);

	Comment getComment(Long postId, Long id);

	Comment updateComment(Long postId, Long id, CommentRequest commentRequest, UserPrincipal currentUser);

	ApiResponse deleteComment(Long postId, Long id, UserPrincipal currentUser);

}
