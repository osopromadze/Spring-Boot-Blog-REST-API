package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") // Loads application-test.properties
@Transactional
public class AlbumControllerTest2 {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Ensure roles exist in the test database
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_USER));
        }
        if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddAlbum_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setTitle("Test Album Title");

        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Album Title"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.photo").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAllAlbums_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create multiple albums
        AlbumRequest albumRequest1 = new AlbumRequest();
        albumRequest1.setTitle("Test Album Title 1");

        AlbumRequest albumRequest2 = new AlbumRequest();
        albumRequest2.setTitle("Test Album Title 2");

        // Add first album
        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Add second album
        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Retrieve all albums
        mockMvc.perform(get("/api/albums")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Album Title 2"))
                .andExpect(jsonPath("$.content[1].title").value("Test Album Title 1"))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAlbumById_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album
        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setTitle("Test Album Title");

        // Add album and capture the ID
        MvcResult result = mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Album Title"))
                .andReturn();

        // Extract the album ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Integer albumId = objectMapper.readTree(responseContent).get("id").asInt();

        // Retrieve the album by ID
        mockMvc.perform(get("/api/albums/{id}", albumId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumId))
                .andExpect(jsonPath("$.title").value("Test Album Title"))
                .andExpect(jsonPath("$.photo").isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdateAlbum_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album
        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setTitle("Original Album Title");

        // Add album and capture the ID
        MvcResult result = mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Original Album Title"))
                .andReturn();

        // Extract the album ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Integer albumId = objectMapper.readTree(responseContent).get("id").asInt();

        // Update the album
        AlbumRequest updateRequest = new AlbumRequest();
        updateRequest.setTitle("Updated Album Title");

        mockMvc.perform(put("/api/albums/{id}", albumId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumId))
                .andExpect(jsonPath("$.title").value("Updated Album Title"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeleteAlbum_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album
        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setTitle("Test Album Title");

        // Add album and capture the ID
        MvcResult result = mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Album Title"))
                .andReturn();

        // Extract the album ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Integer albumId = objectMapper.readTree(responseContent).get("id").asInt();

        // Delete the album
        mockMvc.perform(delete("/api/albums/{id}", albumId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("You successfully deleted album"));

        // Verify the album is deleted by trying to retrieve it
        mockMvc.perform(get("/api/albums/{id}", albumId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Album not found with id: '" + albumId + "'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetUserAlbums_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create multiple albums for the user
        AlbumRequest albumRequest1 = new AlbumRequest();
        albumRequest1.setTitle("User Album Title 1");

        AlbumRequest albumRequest2 = new AlbumRequest();
        albumRequest2.setTitle("User Album Title 2");

        // Add first album
        MvcResult result1 = mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("User Album Title 1"))
                .andReturn();

        // Add second album
        MvcResult result2 = mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("User Album Title 2"))
                .andReturn();

        // Retrieve user albums
        mockMvc.perform(get("/api/users/testuser/albums")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("User Album Title 2"))
                .andExpect(jsonPath("$.content[1].title").value("User Album Title 1"))
                .andExpect(jsonPath("$.size").value(30))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }
   
}
