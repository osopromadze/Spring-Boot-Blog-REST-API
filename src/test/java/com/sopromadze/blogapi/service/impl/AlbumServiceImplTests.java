package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class AlbumServiceImplTests {

    @Mock
    private AppUtils appUtils;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    Pageable pageable;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Test
    public void getAllAlbums_whenNoElementsReturned_thenEmptyListInResponse() {
        Mockito.when(albumRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<AlbumResponse> testResponse = albumService.getAllAlbums(1, 2);

        Assert.assertEquals(0, testResponse.getContent().size());
        Assert.assertEquals(Collections.emptyList(), testResponse.getContent());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getAlbum_whenNoResourceFound_thenThrowException() {
        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        albumService.getAlbum(1L);
    }

    @Test
    public void getAlbum_whenAlbumFound_thenReturn200AndAlbum() {
        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Album()));
        ResponseEntity<Album> response = albumService.getAlbum(1L);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateAlbum_whenNoResourceFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        albumService.updateAlbum(1L, new AlbumRequest(), fakeUserPrincipal);
    }

    @Test(expected = BlogapiException.class)
    public void updateAlbum_whenUserUnauthorized_thenThrowException() {
        try {
            Album testAlbum = new Album();
            AlbumRequest fakeAlbumRequest = new AlbumRequest();
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            fakeUser.setId(1L);
            User albumFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            albumFakeUser.setId(2L);
            testAlbum.setUser(albumFakeUser);

            Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testAlbum));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(fakeUser);

            albumService.updateAlbum(1L, fakeAlbumRequest, fakeUserPrincipal);
        } catch (BlogapiException e) {
            Assert.assertEquals(401, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void updateAlbum_whenAlbumFoundAndUserAuthorized_thenReturnAlbumResponse() {
        Album testAlbum = new Album();
        AlbumRequest fakeAlbumRequest = new AlbumRequest();
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        fakeUser.setId(1L);
        testAlbum.setUser(fakeUser);

        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testAlbum));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(fakeUser);

        ResponseEntity<?> response = albumService.updateAlbum(1L, fakeAlbumRequest, fakeUserPrincipal);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteAlbum_whenNoResourceFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        albumService.deleteAlbum(1L, fakeUserPrincipal);
    }

    @Test(expected = BlogapiException.class)
    public void deleteAlbum_whenUserUnauthorized_thenThrowException() {
        try {
            Album testAlbum = new Album();
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            fakeUser.setId(1L);
            User albumFakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
            albumFakeUser.setId(2L);
            testAlbum.setUser(albumFakeUser);

            Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testAlbum));
            Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(fakeUser);

            albumService.deleteAlbum(1L, fakeUserPrincipal);
        } catch (BlogapiException e) {
            Assert.assertEquals(401, e.getStatus().value());
            Assert.assertNotNull(e.getMessage());
            throw e;
        }
    }

    @Test
    public void deleteAlbum_whenAlbumDeleted_theReturnSuccessResponse() {
        Album testAlbum = new Album();
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User("John", "Doe", "john.doe", "john.doe@example.com", "password123");
        fakeUser.setId(1L);
        testAlbum.setUser(fakeUser);

        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(testAlbum));
        Mockito.when(userRepository.getUser(Mockito.any())).thenReturn(fakeUser);

        ResponseEntity<?> response = albumService.deleteAlbum(1L, fakeUserPrincipal);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }
}
