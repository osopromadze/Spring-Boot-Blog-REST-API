package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class PhotoServiceImplTests {

    @Mock
    private PageRequest pageRequest;

    @Mock
    AppUtils appUtils;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private PhotoServiceImpl photoService;

    @Test
    public void getAllPhotos_whenPhotosEmpty_thenReturnEmptyList() {
        Mockito.when(photoRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<PhotoResponse> response = photoService.getAllPhotos(1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getAllPhotos_whenPhotosFound_thenReturnPhotoResponse() {
        Page<Photo> testPage = Mockito.mock(Page.class);
        Mockito.when(testPage.getTotalPages()).thenReturn(1);
        Mockito.when(testPage.getTotalElements()).thenReturn(1L);
        Mockito.when(testPage.getNumber()).thenReturn(0);
        Mockito.when(testPage.getSize()).thenReturn(1);
        Mockito.when(testPage.getNumberOfElements()).thenReturn(1);
        Mockito.when(testPage.getContent()).thenReturn(createPhotoList());

        Mockito.when(photoRepository.findAll(Mockito.any(Pageable.class))).thenReturn(testPage);

        PagedResponse<PhotoResponse> response = photoService.getAllPhotos(1, 2);

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getPhoto_whenPhotoNotFound_thenThrowException() {
        Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        photoService.getPhoto(1L);
    }

    @Test
    public void getPhoto_whenPhotoFound_thenReturnPhotoResponse() {
        Photo photo = new Photo();
        Album album = new Album();
        album.setId(1L);
        photo.setAlbum(album);

        Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(photo));

        photoService.getPhoto(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updatePhoto_whenAlbumNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        photoService.updatePhoto(1L, new PhotoRequest(), fakeUserPrincipal);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updatePhoto_whenNoPhotoFound_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Album album = new Album();
            PhotoRequest request = new PhotoRequest();
            request.setAlbumId(1L);

            Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(album));
            Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

            photoService.updatePhoto(1L, request, fakeUserPrincipal);
        } catch (ResourceNotFoundException e) {
            Assert.assertEquals("Photo", e.getResourceName());
            throw e;
        }
    }

    @Test
    public void updatePhoto_whenUserAuthorized_thenReturnPhotoResponse() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User();
        fakeUser.setId(1L);
        Album album = new Album();
        album.setId(1L);
        album.setUser(fakeUser);
        Photo photo = new Photo();
        photo.setAlbum(album);
        PhotoRequest request = new PhotoRequest();
        request.setAlbumId(1L);


        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(photo));
        Mockito.when(photoRepository.save(Mockito.any())).thenReturn(photo);

        photoService.updatePhoto(1L, request, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updatePhoto_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User();
            fakeUser.setId(2L);
            Album album = new Album();
            album.setId(1L);
            album.setUser(fakeUser);
            Photo photo = new Photo();
            photo.setAlbum(album);
            PhotoRequest request = new PhotoRequest();
            request.setAlbumId(1L);


            Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(album));
            Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(photo));

            photoService.updatePhoto(1L, request, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertEquals(false, e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addPhoto_whenAlbumNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        PhotoRequest request = new PhotoRequest();
        request.setAlbumId(1L);

        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        photoService.addPhoto(request, fakeUserPrincipal);
    }

    @Test
    public void addPhoto_whenUserMatches_thenReturnPhotoResponse() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User();
        fakeUser.setId(1L);
        Album album = new Album();
        album.setId(1L);
        album.setUser(fakeUser);
        Photo photo = new Photo();
        photo.setAlbum(album);
        PhotoRequest request = new PhotoRequest();
        request.setAlbumId(1L);

        Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.save(Mockito.any())).thenReturn(photo);

        photoService.addPhoto(request, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void addPhoto_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User();
            fakeUser.setId(2L);
            Album album = new Album();
            album.setId(1L);
            album.setUser(fakeUser);
            Photo photo = new Photo();
            photo.setAlbum(album);
            PhotoRequest request = new PhotoRequest();
            request.setAlbumId(1L);

            Mockito.when(albumRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(album));

            photoService.addPhoto(request, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertEquals(false, e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deletePhoto_whenPhotoNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        photoService.deletePhoto(1L, fakeUserPrincipal);
    }

    @Test
    public void deletePhoto_whenUserMatches_thenDeletePhoto() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        User fakeUser = new User();
        fakeUser.setId(1L);
        Album album = new Album();
        album.setId(1L);
        album.setUser(fakeUser);
        Photo photo = new Photo();
        photo.setAlbum(album);
        PhotoRequest request = new PhotoRequest();
        request.setAlbumId(1L);

        Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(photo));

        ApiResponse response = photoService.deletePhoto(1L, fakeUserPrincipal);
        Assert.assertTrue(response.getSuccess());
    }

    @Test(expected = UnauthorizedException.class)
    public void deletePhoto_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            User fakeUser = new User();
            fakeUser.setId(2L);
            Album album = new Album();
            album.setId(1L);
            album.setUser(fakeUser);
            Photo photo = new Photo();
            photo.setAlbum(album);
            PhotoRequest request = new PhotoRequest();
            request.setAlbumId(1L);

            Mockito.when(photoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(photo));
            photoService.deletePhoto(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test
    public void getAllPhotosByAlbum_whenAlbumRequested_thenReturnPhotosInAlbum() {
        Page<Photo> testPage = Mockito.mock(Page.class);
        Mockito.when(testPage.getTotalPages()).thenReturn(1);
        Mockito.when(testPage.getTotalElements()).thenReturn(1L);
        Mockito.when(testPage.getNumber()).thenReturn(0);
        Mockito.when(testPage.getSize()).thenReturn(1);
        Mockito.when(testPage.getNumberOfElements()).thenReturn(1);
        Mockito.when(testPage.getContent()).thenReturn(createPhotoList());

        Mockito.when(photoRepository.findByAlbumId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(testPage);

        PagedResponse<PhotoResponse> response = photoService.getAllPhotosByAlbum(1L, 1, 2);

        Assert.assertNotNull(response);
    }

    private List<Photo> createPhotoList() {
        List<Photo> list = new ArrayList<>();
        Photo photo = new Photo();
        Album fakeAlbum = new Album();
        fakeAlbum.setId(1L);
        photo.setAlbum(fakeAlbum);
        list.add(photo);
        return list;
    }
}
