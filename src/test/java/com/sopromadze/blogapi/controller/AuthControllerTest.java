package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.LoginRequest;
import com.sopromadze.blogapi.payload.SignUpRequest;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.JwtAuthenticationEntryPoint;
import com.sopromadze.blogapi.security.JwtAuthenticationFilter;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import com.sopromadze.blogapi.service.impl.CustomUserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role userRole;
    private Authentication authentication;

    /**
     * Test setup method
     * Initializes common test data and mock configurations used across all test methods.
     * Creates sample LoginRequest, SignUpRequest, JwtAuthenticationResponse, UserIdentityAvailability,
     * and UserProfile objects that will be used for testing authentication and user management functionality.
     */
    @Before
    public void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("Venkatesh");
        signUpRequest.setLastName("Poshanapelli");
        signUpRequest.setUsername("venkat");
        signUpRequest.setEmail("venky@example.com");
        signUpRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("venkat");
        loginRequest.setPassword("password123");

        user = new User("Venkatesh", "Poshanapelli", "venkat", "venky@example.com", "encodedPassword");
        user.setId(1L);

        userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);

            authentication = new UsernamePasswordAuthenticationToken("venkat", "password123");
    }

    /**
     * Test successful user registration
     * Verifies that a new user can be registered with valid data and returns the created user
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(roleRepository.findByName(RoleName.ROLE_USER)).willReturn(Optional.of(userRole));
        given(userRepository.count()).willReturn(1L);
        given(userRepository.save(any(User.class))).willReturn(user);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());
    }

    /**
     * Test user registration with already taken username
     * Verifies that the system properly rejects registration when username is already taken
     */
    @Test
    public void testRegisterUser_UsernameAlreadyTaken() throws Exception {
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test user registration with already taken email
     * Verifies that the system properly rejects registration when email is already taken
     */
    @Test
    public void testRegisterUser_EmailAlreadyTaken() throws Exception {
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test user registration with invalid data
     * Verifies that the system properly validates and rejects invalid registration data
     */
    @Test
    public void testRegisterUser_InvalidData() throws Exception {
        SignUpRequest invalidRequest = new SignUpRequest();
        invalidRequest.setFirstName("Jo"); 
        invalidRequest.setLastName("Do"); 
        invalidRequest.setUsername("jo"); 
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("12345");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test successful user authentication
     * Verifies that a user can authenticate with valid credentials and receive a JWT token
     */
    @Test
    public void testAuthenticateUser_Success() throws Exception {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtTokenProvider.generateToken(any(Authentication.class))).willReturn("jwt-token");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test user authentication with invalid credentials
     * Verifies that the system properly rejects authentication with wrong username/password
     */
    @Test
    public void testAuthenticateUser_InvalidCredentials() throws Exception {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test user authentication with invalid data
     * Verifies that the system properly validates and rejects invalid authentication data
     */
    @Test
    public void testAuthenticateUser_InvalidData() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsernameOrEmail(""); // Blank
        invalidRequest.setPassword(""); // Blank

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
