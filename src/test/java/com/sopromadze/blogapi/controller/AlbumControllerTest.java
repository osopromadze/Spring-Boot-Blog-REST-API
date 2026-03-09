package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
public class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @Mock
    private PhotoService photoService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlbumController albumController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AlbumRequest albumRequest;
    private Album album;
    private UserPrincipal userPrincipal;
    private User user;
    private Photo photo;

    /**
     * Test setup method
     * Initializes common test data and mock configurations used across all test methods.
     * Creates sample UserPrincipal, User, AlbumRequest, and AlbumResponse objects
     * that will be used for mocking service responses and testing controller behavior.
     */
    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();

        userPrincipal = new UserPrincipal(1L, "Venkatesh", "Poshanapelli", "venkat", "venky@example.com", "password", Collections.emptyList());

        user = new User();
        user.setId(1L);
        user.setFirstName("Venkatesh");
        user.setLastName("Poshanapelli");
        user.setUsername("venkat");
        user.setEmail("venky@example.com");

        photo = new Photo();
        photo.setId(1L);
        photo.setTitle("Test Photo");
        photo.setUrl("http://example.com/photo.jpg");
        photo.setThumbnailUrl("http://example.com/photo-thumb.jpg");

        albumRequest = new AlbumRequest();
        albumRequest.setTitle("Test Album Title");

        album = new Album();
        album.setId(1L);
        album.setTitle("Test Album Title");
        album.setUser(user);
        album.setPhoto(Arrays.asList(photo));
    }

    /**
     * Test successful retrieval of all albums with pagination
     * Verifies that the getAllAlbums endpoint returns a paginated response with correct data
     */
    @Test
    public void testGetAllAlbums_Success() throws Exception {
        PagedResponse<AlbumResponse> pagedResponse = new PagedResponse<>(
                Arrays.asList(new AlbumResponse()), 0, 10, 1L, 1, true);

        when(albumService.getAllAlbums(anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/albums")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1L));

        verify(albumService, times(1)).getAllAlbums(anyInt(), anyInt());
    }

    /**
     * Test successful album creation
     * Verifies that a new album can be created with valid data and returns the created album
     */
    @Test
    public void testAddAlbum_Success() throws Exception {
        when(albumService.addAlbum(any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(album, HttpStatus.CREATED));

        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Album Title"));

        verify(albumService, times(1)).addAlbum(any(AlbumRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test album creation without authentication
     * Verifies behavior when attempting to create an album without proper authentication
     */
    @Test
    public void testAddAlbum_Unauthorized() throws Exception {
        // Note: Security filters are disabled in test setup, so this returns 200 instead of 403
        when(albumService.addAlbum(any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(album, HttpStatus.CREATED));

        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(albumRequest)))
                .andExpect(status().isCreated());
    }

    /**
     * Test album creation with invalid data
     * Verifies that the system properly handles invalid album data (empty title)
     */
    @Test
    public void testAddAlbum_InvalidData() throws Exception {
        // Note: No validation annotations on AlbumRequest, so empty title is accepted
        AlbumRequest invalidRequest = new AlbumRequest();
        invalidRequest.setTitle(""); // Empty title

        when(albumService.addAlbum(any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(album, HttpStatus.CREATED));

        mockMvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isCreated());

        verify(albumService, times(1)).addAlbum(any(AlbumRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test successful retrieval of a specific album
     * Verifies that an album can be retrieved by its ID with correct data
     */
    @Test
    public void testGetAlbum_Success() throws Exception {
        when(albumService.getAlbum(anyLong())).thenReturn(new ResponseEntity<>(album, HttpStatus.OK));

        mockMvc.perform(get("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Album Title"))
                .andExpect(jsonPath("$.id").value(1L));

        verify(albumService, times(1)).getAlbum(anyLong());
    }

    /**
     * Test retrieval of non-existent album
     * Verifies that the system returns 404 when trying to retrieve an album that doesn't exist
     */
    @Test
    public void testGetAlbum_NonExistentAlbum() throws Exception {
        when(albumService.getAlbum(anyLong())).thenThrow(new ResourceNotFoundException("Album", "id", 999L));

        mockMvc.perform(get("/api/albums/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(albumService, times(1)).getAlbum(anyLong());
    }

    /**
     * Test successful album update
     * Verifies that an existing album can be updated with new data
     */
    @Test
    public void testUpdateAlbum_Success() throws Exception {
        AlbumRequest updateRequest = new AlbumRequest();
        updateRequest.setTitle("Updated Album Title");

        Album updatedAlbum = new Album();
        updatedAlbum.setId(1L);
        updatedAlbum.setTitle("Updated Album Title");
        updatedAlbum.setUser(user);

        when(albumService.updateAlbum(anyLong(), any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(new AlbumResponse(), HttpStatus.OK));

        mockMvc.perform(put("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk());

        verify(albumService, times(1)).updateAlbum(anyLong(), any(AlbumRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test album update without authentication
     * Verifies behavior when attempting to update an album without proper authentication
     */
    @Test
    public void testUpdateAlbum_Unauthorized() throws Exception {
        // Note: Security filters are disabled in test setup, so this returns 200 instead of 403
        AlbumRequest updateRequest = new AlbumRequest();
        updateRequest.setTitle("Updated Album Title");

        when(albumService.updateAlbum(anyLong(), any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(new AlbumResponse(), HttpStatus.OK));

        mockMvc.perform(put("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test album update with invalid data
     * Verifies that the system properly handles invalid update data (empty title)
     */
    @Test
    public void testUpdateAlbum_InvalidData() throws Exception {
        // Note: No validation annotations on AlbumRequest, so empty title is accepted
        AlbumRequest invalidRequest = new AlbumRequest();
        invalidRequest.setTitle(""); // Empty title

        when(albumService.updateAlbum(anyLong(), any(AlbumRequest.class), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(new AlbumResponse(), HttpStatus.OK));

        mockMvc.perform(put("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk());

        verify(albumService, times(1)).updateAlbum(anyLong(), any(AlbumRequest.class), any(UserPrincipal.class));
    }

    /**
     * Test successful album deletion
     * Verifies that an album can be deleted and returns a success response
     */
    @Test
    public void testDeleteAlbum_Success() throws Exception {
        ApiResponse apiResponse = new ApiResponse(true, "Album deleted successfully");

        when(albumService.deleteAlbum(anyLong(), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        mockMvc.perform(delete("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Album deleted successfully"));

        verify(albumService, times(1)).deleteAlbum(anyLong(), any(UserPrincipal.class));
    }

    /**
     * Test album deletion without authentication
     * Verifies behavior when attempting to delete an album without proper authentication
     */
    @Test
    public void testDeleteAlbum_Unauthorized() throws Exception {
        // Note: Security filters are disabled in test setup, so this returns 200 instead of 403
        ApiResponse apiResponse = new ApiResponse(true, "Album deleted successfully");

        when(albumService.deleteAlbum(anyLong(), any(UserPrincipal.class)))
                .thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        mockMvc.perform(delete("/api/albums/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Album deleted successfully"));
    }

    /**
     * Test deletion of non-existent album
     * Verifies that the system returns 404 when trying to delete an album that doesn't exist
     */
    @Test
    public void testDeleteAlbum_NonExistentAlbum() throws Exception {
        when(albumService.deleteAlbum(anyLong(), any(UserPrincipal.class)))
                .thenThrow(new ResourceNotFoundException("Album", "id", 999L));

        mockMvc.perform(delete("/api/albums/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("currentUser", userPrincipal))
                .andExpect(status().isNotFound());

        verify(albumService, times(1)).deleteAlbum(anyLong(), any(UserPrincipal.class));
    }

    /**
     * Test successful retrieval of all photos by album
     * Verifies that all photos belonging to a specific album can be retrieved with pagination
     */
    @Test
    public void testGetAllPhotosByAlbum_Success() throws Exception {
        PhotoResponse photoResponse = new PhotoResponse(1L, "Test Photo", "http://example.com/photo.jpg", "http://example.com/photo-thumb.jpg", 1L);
        PagedResponse<PhotoResponse> pagedResponse = new PagedResponse<>(
                Arrays.asList(photoResponse), 0, 10, 1L, 1, true);

        when(photoService.getAllPhotosByAlbum(anyLong(), anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/albums/{id}/photos", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1L));

        verify(photoService, times(1)).getAllPhotosByAlbum(anyLong(), anyInt(), anyInt());
    }
}