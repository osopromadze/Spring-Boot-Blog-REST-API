package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TodoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class TodoServiceImplTests {

    @Mock
    private AppUtils appUtils;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private UserPrincipal fakeUserPrincipal;

    @Before
    public void setup() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void completeTodo_whenTodoNotFound_thenThrowException() {
        Mockito.when(todoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        todoService.completeTodo(1L, fakeUserPrincipal);
    }

    @Test
    public void completeTodo_whenUserMatches_thenSaveTodo() {
        User testUser = new User();
        testUser.setId(1L);
        Todo todo = new Todo();
        todo.setUser(testUser);

        Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

        todoService.completeTodo(1L, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void completeTodo_whenUserNotAuthorized_thenThrowException() {
        try {
            User testUser = new User();
            testUser.setId(1L);
            User todoUser = new User();
            todoUser.setId(2L);
            Todo todo = new Todo();
            todo.setUser(todoUser);

            Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

            todoService.completeTodo(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void unCompleteTodo_whenTodoNotFound_thenThrowException() {
        Mockito.when(todoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        todoService.unCompleteTodo(1L, fakeUserPrincipal);
    }

    @Test
    public void unCompleteTodo_whenUserMatches_thenSaveTodo() {
        User testUser = new User();
        testUser.setId(1L);
        Todo todo = new Todo();
        todo.setUser(testUser);

        Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

        todoService.unCompleteTodo(1L, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void unCompleteTodo_whenUserNotAuthorized_thenThrowException() {
        try {
            User testUser = new User();
            testUser.setId(1L);
            User todoUser = new User();
            todoUser.setId(2L);
            Todo todo = new Todo();
            todo.setUser(todoUser);

            Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

            todoService.unCompleteTodo(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test
    public void getAllTodos_whenNoTodoFound_thenReturnEmptyList() {
        Mockito.when(todoRepository.findByCreatedBy(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Todo> response = todoService.getAllTodos(fakeUserPrincipal, 1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getAllTodos_whenTodoFound_thenReturnContent() {
        Page<Todo> page = Mockito.mock(Page.class);

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createTodoList());

        Mockito.when(todoRepository.findByCreatedBy(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(page);

        PagedResponse<Todo> response = todoService.getAllTodos(fakeUserPrincipal, 1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTodo_whenTodoNotFound_thenThrowException() {
        Mockito.when(todoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        todoService.getTodo(1L, fakeUserPrincipal);
    }

    @Test
    public void getTodo_whenUserMatches_thenReturnTodo() {
        User testUser = new User();
        testUser.setId(1L);
        Todo todo = new Todo();
        todo.setUser(testUser);

        Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

        todoService.getTodo(1L, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void getTodo_whenUserNotAuthorized_thenThrowException() {
        try {
            User testUser = new User();
            testUser.setId(1L);
            User todoUser = new User();
            todoUser.setId(2L);
            Todo todo = new Todo();
            todo.setUser(todoUser);

            Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

            todoService.getTodo(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateTodo_whenTodoNotFound_thenThrowException() {
        Mockito.when(todoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        todoService.updateTodo(1L, new Todo(), fakeUserPrincipal);
    }

    @Test
    public void updateTodo_whenUserMatches_thenSaveTodo() {
        User testUser = new User();
        testUser.setId(1L);
        Todo todo = new Todo();
        todo.setUser(testUser);

        Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

        todoService.updateTodo(1L, todo, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updateTodo_whenUserNotAuthorized_thenThrowException() {
        try {
            User testUser = new User();
            testUser.setId(1L);
            User todoUser = new User();
            todoUser.setId(2L);
            Todo todo = new Todo();
            todo.setUser(todoUser);

            Mockito.when(todoRepository.findById(Mockito.any())).thenReturn(Optional.of(todo));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(testUser);

            todoService.updateTodo(1L, todo, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    private List<Todo> createTodoList() {
        List<Todo> list = new ArrayList<>();
        Todo todo = new Todo();
        list.add(todo);
        return list;
    }
}
