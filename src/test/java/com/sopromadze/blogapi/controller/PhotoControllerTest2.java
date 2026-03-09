package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.AlbumRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test") // Loads application-test.properties
@Transactional
public class PhotoControllerTest2 {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AlbumRepository albumRepository;

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
    public void testAddPhoto_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create photo request
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Test Photo Title");
        photoRequest.setUrl("http://example.com/photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/photo-thumb.jpg");
        photoRequest.setAlbumId(album.getId());

        // Add photo
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Photo Title"))
                .andExpect(jsonPath("$.url").value("http://example.com/photo.jpg"))
                .andExpect(jsonPath("$.thumbnailUrl").value("http://example.com/photo-thumb.jpg"))
                .andExpect(jsonPath("$.albumId").value(album.getId()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddPhoto_InvalidData() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create photo request with invalid data (empty title)
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle(""); // Empty title (invalid)
        photoRequest.setUrl("http://example.com/photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/photo-thumb.jpg");
        photoRequest.setAlbumId(album.getId());

        // Attempt to add photo with invalid data
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddPhoto_MissingAlbum() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create photo request with non-existent album ID
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Test Photo Title");
        photoRequest.setUrl("http://example.com/photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/photo-thumb.jpg");
        photoRequest.setAlbumId(999L); // Non-existent album ID

        // Attempt to add photo with non-existent album
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Album not found with id: '999'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testAddPhoto_MissingRequiredFields() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create photo request with missing required fields
        PhotoRequest photoRequest = new PhotoRequest();
        // Missing title, url, thumbnailUrl, and albumId

        // Attempt to add photo with missing required fields
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetPhotoById_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create a photo request
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Test Photo Title");
        photoRequest.setUrl("http://example.com/photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/photo-thumb.jpg");
        photoRequest.setAlbumId(album.getId());

        // Add photo to get its ID
        MvcResult result = mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the photo ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long photoId = objectMapper.readTree(responseContent).get("id").asLong();

        // Test retrieving the photo by ID
        mockMvc.perform(get("/api/photos/{id}", photoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(photoId))
                .andExpect(jsonPath("$.title").value("Test Photo Title"))
                .andExpect(jsonPath("$.url").value("http://example.com/photo.jpg"))
                .andExpect(jsonPath("$.thumbnailUrl").value("http://example.com/photo-thumb.jpg"))
                .andExpect(jsonPath("$.albumId").value(album.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetPhotoById_NotFound() throws Exception {
        // Test retrieving a non-existent photo
        mockMvc.perform(get("/api/photos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Photo not found with id: '999'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdatePhoto_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create a photo request
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Original Photo Title");
        photoRequest.setUrl("http://example.com/original-photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/original-photo-thumb.jpg");
        photoRequest.setAlbumId(album.getId());

        // Add photo to get its ID
        MvcResult result = mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the photo ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long photoId = objectMapper.readTree(responseContent).get("id").asLong();

        // Create updated photo request
        PhotoRequest updatedPhotoRequest = new PhotoRequest();
        updatedPhotoRequest.setTitle("Updated Photo Title");
        updatedPhotoRequest.setUrl("http://example.com/updated-photo.jpg");
        updatedPhotoRequest.setThumbnailUrl("http://example.com/updated-photo-thumb.jpg");
        updatedPhotoRequest.setAlbumId(album.getId());

        // Test updating the photo
        mockMvc.perform(put("/api/photos/{id}", photoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPhotoRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(photoId))
                .andExpect(jsonPath("$.title").value("Updated Photo Title"))
                .andExpect(jsonPath("$.url").value("http://example.com/updated-photo.jpg"))
                .andExpect(jsonPath("$.thumbnailUrl").value("http://example.com/updated-photo-thumb.jpg"))
                .andExpect(jsonPath("$.albumId").value(album.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testUpdatePhoto_NotFound() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create updated photo request
        PhotoRequest updatedPhotoRequest = new PhotoRequest();
        updatedPhotoRequest.setTitle("Updated Photo Title");
        updatedPhotoRequest.setUrl("http://example.com/updated-photo.jpg");
        updatedPhotoRequest.setThumbnailUrl("http://example.com/updated-photo-thumb.jpg");
        updatedPhotoRequest.setAlbumId(album.getId());

        // Test updating a non-existent photo
        mockMvc.perform(put("/api/photos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPhotoRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Photo not found with id: '999'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeletePhoto_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photo
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create a photo request
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle("Test Photo Title");
        photoRequest.setUrl("http://example.com/photo.jpg");
        photoRequest.setThumbnailUrl("http://example.com/photo-thumb.jpg");
        photoRequest.setAlbumId(album.getId());

        // Add photo to get its ID
        MvcResult result = mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract the photo ID from the response
        String responseContent = result.getResponse().getContentAsString();
        Long photoId = objectMapper.readTree(responseContent).get("id").asLong();

        // Test deleting the photo
        mockMvc.perform(delete("/api/photos/{id}", photoId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Photo deleted successfully"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testDeletePhoto_NotFound() throws Exception {
        // Test deleting a non-existent photo
        mockMvc.perform(delete("/api/photos/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Photo not found with id: '999'"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAllPhotosByAlbum_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photos
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create multiple photos for testing pagination
        PhotoRequest photoRequest1 = new PhotoRequest();
        photoRequest1.setTitle("Test Photo 1");
        photoRequest1.setUrl("http://example.com/photo1.jpg");
        photoRequest1.setThumbnailUrl("http://example.com/photo1-thumb.jpg");
        photoRequest1.setAlbumId(album.getId());

        PhotoRequest photoRequest2 = new PhotoRequest();
        photoRequest2.setTitle("Test Photo 2");
        photoRequest2.setUrl("http://example.com/photo2.jpg");
        photoRequest2.setThumbnailUrl("http://example.com/photo2-thumb.jpg");
        photoRequest2.setAlbumId(album.getId());

        // Add photos to the database
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest2)))
                .andExpect(status().isOk());

        // Test retrieving all photos by album
        mockMvc.perform(get("/api/albums/{albumId}/photos", album.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Photo 2")) // Most recent first
                .andExpect(jsonPath("$.content[0].url").value("http://example.com/photo2.jpg"))
                .andExpect(jsonPath("$.content[0].thumbnailUrl").value("http://example.com/photo2-thumb.jpg"))
                .andExpect(jsonPath("$.content[0].albumId").value(album.getId()))
                .andExpect(jsonPath("$.content[1].title").value("Test Photo 1")) // Older photo second
                .andExpect(jsonPath("$.content[1].url").value("http://example.com/photo1.jpg"))
                .andExpect(jsonPath("$.content[1].thumbnailUrl").value("http://example.com/photo1-thumb.jpg"))
                .andExpect(jsonPath("$.content[1].albumId").value(album.getId()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(30)) // Default page size
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetAllPhotos_Success() throws Exception {
        // Create a user in the database that matches the mock user
        User user = new User("John", "Doe", "testuser", "john.doe@example.com", "password123");
        userRepository.save(user);

        // Create an album for the photos
        Album album = new Album();
        album.setTitle("Test Album");
        album.setUser(user);
        album = albumRepository.save(album);

        // Create multiple photos for testing pagination
        PhotoRequest photoRequest1 = new PhotoRequest();
        photoRequest1.setTitle("Test Photo 1");
        photoRequest1.setUrl("http://example.com/photo1.jpg");
        photoRequest1.setThumbnailUrl("http://example.com/photo1-thumb.jpg");
        photoRequest1.setAlbumId(album.getId());

        PhotoRequest photoRequest2 = new PhotoRequest();
        photoRequest2.setTitle("Test Photo 2");
        photoRequest2.setUrl("http://example.com/photo2.jpg");
        photoRequest2.setThumbnailUrl("http://example.com/photo2-thumb.jpg");
        photoRequest2.setAlbumId(album.getId());

        // Add photos to the database
        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(photoRequest2)))
                .andExpect(status().isOk());

        // Test retrieving all photos
        mockMvc.perform(get("/api/photos")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Photo 2")) // Most recent first
                .andExpect(jsonPath("$.content[0].url").value("http://example.com/photo2.jpg"))
                .andExpect(jsonPath("$.content[0].thumbnailUrl").value("http://example.com/photo2-thumb.jpg"))
                .andExpect(jsonPath("$.content[0].albumId").value(album.getId()))
                .andExpect(jsonPath("$.content[1].title").value("Test Photo 1")) // Older photo second
                .andExpect(jsonPath("$.content[1].url").value("http://example.com/photo1.jpg"))
                .andExpect(jsonPath("$.content[1].thumbnailUrl").value("http://example.com/photo1-thumb.jpg"))
                .andExpect(jsonPath("$.content[1].albumId").value(album.getId()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(30)) // Default page size
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }
}
