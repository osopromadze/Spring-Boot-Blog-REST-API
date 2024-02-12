package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface CommentRepository  {
	Page<Comment> findByPostId(Long postId, Pageable pageable);

	Comment save(Comment comment);

	Optional<Comment> findById(Long id);

	void deleteById(Long id);

}
