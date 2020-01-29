package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface PhotoService {

	PagedResponse<PhotoResponse> getAllPhotos(int page, int size);

	ResponseEntity<?> getPhoto(Long id);

	ResponseEntity<?> updatePhoto(Long id, PhotoRequest photoRequest, UserPrincipal currentUser);

	ResponseEntity<?> addPhoto(PhotoRequest photoRequest, UserPrincipal currentUser);

	ResponseEntity<?> deletePhoto(Long id, UserPrincipal currentUser);

	PagedResponse<PhotoResponse> getAllPhotosByAlbum(Long albumId, int page, int size);

}