package com.sopromadze.blogapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.todo.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	Page<Todo> findByCreatedBy(Long userId, Pageable pageable);
}
