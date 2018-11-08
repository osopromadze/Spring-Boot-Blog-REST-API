package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.todo.Todo;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TodoService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<Todo> getAllTodos(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return todoService.getAllTodos(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addTodo(@Valid @RequestBody Todo todo, @CurrentUser UserPrincipal currentUser){
        return todoService.addTodo(todo, currentUser);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return todoService.getTodo(id, currentUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateTodo(@PathVariable(value = "id") Long id, @Valid @RequestBody Todo newTodo, @CurrentUser UserPrincipal currentUser){
        return todoService.updateTodo(id, newTodo, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return todoService.deleteTodo(id, currentUser);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> completeTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return todoService.completeTodo(id, currentUser);
    }

    @PutMapping("/{id}/unComplete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> unCompleteTodo(@PathVariable(value = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return todoService.unCompleteTodo(id, currentUser);
    }
}
