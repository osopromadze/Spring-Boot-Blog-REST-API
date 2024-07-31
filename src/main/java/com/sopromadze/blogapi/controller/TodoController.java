package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TodoService;
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

@Tag(name = "9- Todos", description = "Operations related to todos")
@RestController
@RequestMapping("/api/todos")
public class TodoController {

	@Autowired
	private TodoService todoService;

	@Operation(description = "Get all todos which belongs to logged in user", summary = "Get all todos")
	@GetMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<PagedResponse<Todo>> getAllTodos(
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

		PagedResponse<Todo> response = todoService.getAllTodos(currentUser, page, size);

		return new ResponseEntity< >(response, HttpStatus.OK);
	}

	@Operation(description = "Create new todo (By logged in user)", summary = "Create todo")
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Todo> addTodo(@Valid @RequestBody Todo todo, @CurrentUser UserPrincipal currentUser) {
		Todo newTodo = todoService.addTodo(todo, currentUser);

		return new ResponseEntity< >(newTodo, HttpStatus.CREATED);
	}

	@Operation(description = "Get todo by id (If todo belongs to logged in user)", summary = "Get todo")
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Todo> getTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		Todo todo = todoService.getTodo(id, currentUser);

		return new ResponseEntity< >(todo, HttpStatus.OK);
	}

	@Operation(description = "Update todo (If todo belongs to logged in user)", summary = "Update todo")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Todo> updateTodo(@PathVariable(value = "id") Long id, @Valid @RequestBody Todo newTodo,
			@CurrentUser UserPrincipal currentUser) {
		Todo updatedTodo = todoService.updateTodo(id, newTodo, currentUser);

		return new ResponseEntity< >(updatedTodo, HttpStatus.OK);
	}

	@Operation(description = "Delete todo (If todo belongs to logged in user)", summary = "delete todo")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse> deleteTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		ApiResponse apiResponse = todoService.deleteTodo(id, currentUser);

		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@Operation(description = "Mark todo as complete (If todo belongs to logged in user)", summary = "Complete todo")
	@PutMapping("/{id}/complete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Todo> completeTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser) {

		Todo todo = todoService.completeTodo(id, currentUser);

		return new ResponseEntity< >(todo, HttpStatus.OK);
	}

	@Operation(description = "Mark todo as incomplete (If todo belongs to logged in user)", summary = "Incomplete todo")
	@PutMapping("/{id}/unComplete")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Todo> unCompleteTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser) {

		Todo todo = todoService.unCompleteTodo(id, currentUser);

		return new ResponseEntity< >(todo, HttpStatus.OK);
	}
}
