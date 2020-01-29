package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.model.todo.Todo;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface TodoService {

	ResponseEntity<?> completeTodo(Long id, UserPrincipal currentUser);

	ResponseEntity<?> unCompleteTodo(Long id, UserPrincipal currentUser);

	PagedResponse<Todo> getAllTodos(UserPrincipal currentUser, int page, int size);

	ResponseEntity<?> addTodo(Todo todo, UserPrincipal currentUser);

	ResponseEntity<?> getTodo(Long id, UserPrincipal currentUser);

	ResponseEntity<?> updateTodo(Long id, Todo newTodo, UserPrincipal currentUser);

	ResponseEntity<?> deleteTodo(Long id, UserPrincipal currentUser);

}