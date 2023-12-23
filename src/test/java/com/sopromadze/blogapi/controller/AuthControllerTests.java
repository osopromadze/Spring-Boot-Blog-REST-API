package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.exception.AppException;
import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.SignUpRequest;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    private SignUpRequest signUpRequest;

    @Before
    public void setup() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("john.doe");
        signUpRequest.setEmail("john.doe@example.com");
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setPassword("Password123!");
    }

    @Test(expected = BlogapiException.class)
    public void registerUser_whenUsernameTaken_thenThrowException() {
        try {
            Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.TRUE);

            authController.registerUser(signUpRequest);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertEquals("Username is already taken", e.getMessage());
            throw e;
        }
    }

    @Test(expected = BlogapiException.class)
    public void registerUser_whenEmailTaken_thenThrowException() {
        try {
            Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
            Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.TRUE);

            authController.registerUser(signUpRequest);
        } catch (BlogapiException e) {
            Assert.assertEquals(400, e.getStatus().value());
            Assert.assertEquals("Email is already taken", e.getMessage());
            throw e;
        }
    }

    @Test(expected = AppException.class)
    public void registerUser_whenNoUsersAndUserRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("Encoded");
        Mockito.when(userRepository.count()).thenReturn(0L);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.empty());

        authController.registerUser(signUpRequest);
    }

    @Test(expected = AppException.class)
    public void registerUser_whenNoUsersAndAdminRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("Encoded");
        Mockito.when(userRepository.count()).thenReturn(0L);
        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(new Role()));
        Mockito.when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.empty());

        authController.registerUser(signUpRequest);
    }

    @Test(expected = AppException.class)
    public void registerUser_whenUsersAddedAndUserRoleNotFound_thenThrowException() {
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("Encoded");
        Mockito.when(userRepository.count()).thenReturn(1L);
        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        authController.registerUser(signUpRequest);
    }

    @Test
    public void registerUser_whenUserRegistered_thenReturnSuccessResponse() {
        User user = new User();
        user.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("Encoded");
        Mockito.when(userRepository.count()).thenReturn(1L);
        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(new Role()));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        ResponseEntity<ApiResponse> response = authController.registerUser(signUpRequest);
        Assert.assertTrue(Objects.requireNonNull(response.getBody()).getSuccess());
    }

    @Test
    public void registerUser_whenNoUsersYetAndUserRegistered_thenReturnSuccessResponse() {
        User user = new User();
        user.setId(1L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("Encoded");
        Mockito.when(userRepository.count()).thenReturn(0L);
        Mockito.when(roleRepository.findByName(Mockito.any())).thenReturn(Optional.of(new Role()));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        ResponseEntity<ApiResponse> response = authController.registerUser(signUpRequest);
        Assert.assertTrue(Objects.requireNonNull(response.getBody()).getSuccess());
    }
}
