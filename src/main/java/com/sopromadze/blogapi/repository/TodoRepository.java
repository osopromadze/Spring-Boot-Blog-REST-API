package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface TodoRepository  {
	Page<Todo> findByCreatedBy(Long userId, Pageable pageable);

	Optional<Todo> findById(Long id);

	Todo save(Todo todo);

	void deleteById(Long id);

}
