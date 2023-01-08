package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.payload.response.ApiResponse;
import com.sopromadze.blogapi.payload.response.PagedResponse;
import com.sopromadze.blogapi.payload.request.PhotoRequest;
import com.sopromadze.blogapi.payload.response.PhotoResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface PhotoService {

	PagedResponse<PhotoResponse> getAllPhotos(int page, int size);

	PhotoResponse getPhoto(Long id);

	PhotoResponse updatePhoto(Long id, PhotoRequest photoRequest, UserPrincipal currentUser);

	PhotoResponse addPhoto(PhotoRequest photoRequest, UserPrincipal currentUser);

	ApiResponse deletePhoto(Long id, UserPrincipal currentUser);

	PagedResponse<PhotoResponse> getAllPhotosByAlbum(Long albumId, int page, int size);

}