package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.payload.UserSummary;
import com.sopromadze.blogapi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @Before
    public void setUp() {
        user = new User("John", "Doe", "johndoe", "john.doe@example.com", "password123");
        user.setId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_Success() throws Exception {
        given(userService.addUser(any(User.class))).willReturn(user);

        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }
             // Negative test cases for AddUser
    @Test
    @WithMockUser(roles = "USER")
    public void testAddUser_AccessDenied() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access is denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_MissingFirstName() throws Exception {
        String userJson = "{"
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_MissingLastName() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_MissingUsername() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"email\":\"john.doe@example.com\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_MissingPassword() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"john.doe@example.com\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_MissingEmail() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser_InvalidEmail() throws Exception {
        String userJson = "{"
                + "\"firstName\":\"John\","
                + "\"lastName\":\"Doe\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"invalid-email\","
                + "\"password\":\"password123\""
                + "}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testUpdateUser_Success() throws Exception {
        User updatedUser = new User("Jane", "Smith", "johndoe", "jane.smith@example.com", "newpassword");
        updatedUser.setId(1L);

        given(userService.updateUser(any(User.class), anyString(), any())).willReturn(updatedUser);

        String updateJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword\""
                + "}";

        mockMvc.perform(put("/api/users/johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }
            // Negative test cases for UpdateUser
    @Test
    public void testUpdateUser_Unauthenticated() throws Exception {
        String updateJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword\""
                + "}";

        mockMvc.perform(put("/api/users/johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testUpdateUser_MissingFirstName() throws Exception {
        String updateJson = "{"
                + "\"lastName\":\"Smith\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"jane.smith@example.com\","
                + "\"password\":\"newpassword\""
                + "}";

        mockMvc.perform(put("/api/users/johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testUpdateUser_InvalidEmail() throws Exception {
        String updateJson = "{"
                + "\"firstName\":\"Jane\","
                + "\"lastName\":\"Smith\","
                + "\"username\":\"johndoe\","
                + "\"email\":\"invalid-email\","
                + "\"password\":\"newpassword\""
                + "}";

        mockMvc.perform(put("/api/users/johndoe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "johndoe", roles = "USER")
    public void testDeleteUser_Success() throws Exception {
        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "User deleted successfully");

        given(userService.deleteUser(anyString(), any())).willReturn(apiResponse);

        mockMvc.perform(delete("/api/users/johndoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    public void testDeleteUser_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/users/johndoe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "janedoe", roles = "USER")
    public void testDeleteUser_AccessDenied() throws Exception {
        // Testing scenario where user tries to delete another user's account
        // Mock service to throw exception for access denied
        doThrow(new RuntimeException("Access is denied")).when(userService).deleteUser(anyString(), any());

        mockMvc.perform(delete("/api/users/johndoe"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access is denied"));
    }
}
