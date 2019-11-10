package com.sopromadze.blogapi.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.todo.Todo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TodoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppConstants;

@Service
public class TodoService {
	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<?> completeTodo(Long id, UserPrincipal currentUser) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		if (todo.getUser().getId().equals(user.getId())) {
			todo.setCompleted(true);
			Todo result = todoRepository.save(todo);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return todo.getUser().getId().equals(user.getId()) ? new ResponseEntity<>(todo, HttpStatus.OK)
				: new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"),
						HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> unCompleteTodo(Long id, UserPrincipal currentUser) {
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		if (todo.getUser().getId().equals(user.getId())) {
			todo.setCompleted(false);
			Todo result = todoRepository.save(todo);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return todo.getUser().getId().equals(user.getId()) ? new ResponseEntity<>(todo, HttpStatus.OK)
				: new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"),
						HttpStatus.UNAUTHORIZED);
	}

	public PagedResponse<Todo> getAllTodos(UserPrincipal currentUser, int page, int size) {
		validatePageNumberAndSize(page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Todo> todos = todoRepository.findByCreatedBy(currentUser.getId(), pageable);

		if (todos.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), todos.getNumber(), todos.getSize(),
					todos.getTotalElements(), todos.getTotalPages(), todos.isLast());
		}

		return new PagedResponse<>(todos.getContent(), todos.getNumber(), todos.getSize(), todos.getTotalElements(),
				todos.getTotalPages(), todos.isLast());
	}

	public ResponseEntity<?> addTodo(Todo todo, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		todo.setUser(user);
		Todo result = todoRepository.save(todo);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	public ResponseEntity<?> getTodo(Long id, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));

		return todo.getUser().getId().equals(user.getId()) ? new ResponseEntity<>(todo, HttpStatus.OK)
				: new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"),
						HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> updateTodo(Long id, Todo newTodo, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));
		if (todo.getUser().getId().equals(user.getId())) {
			todo.setTitle(newTodo.getTitle());
			todo.setCompleted(newTodo.getCompleted());
			Todo result = todoRepository.save(todo);
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> deleteTodo(Long id, UserPrincipal currentUser) {
		User user = userRepository.findByUsername(currentUser.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
		Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo", "id", id));

		if (todo.getUser().getId().equals(user.getId())) {
			todoRepository.deleteById(id);
			return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted todo"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"),
				HttpStatus.UNAUTHORIZED);
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
