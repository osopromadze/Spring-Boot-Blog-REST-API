package com.sopromadze.blogapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.photo.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppConstants;

@Service
public class PhotoService {
	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private AlbumRepository albumRepository;

	public PagedResponse<PhotoResponse> getAllPhotos(int page, int size) {
		validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
		Page<Photo> photos = photoRepository.findAll(pageable);

		List<PhotoResponse> photoResponses = new ArrayList<>();
		for (Photo photo : photos.getContent()) {
			photoResponses.add(new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
					photo.getThumbnailUrl(), photo.getAlbum().getId()));
		}

		if (photos.getNumberOfElements() == 0) {
			return new PagedResponse<>(Collections.emptyList(), photos.getNumber(), photos.getSize(),
					photos.getTotalElements(), photos.getTotalPages(), photos.isLast());
		}
		return new PagedResponse<>(photoResponses, photos.getNumber(), photos.getSize(), photos.getTotalElements(),
				photos.getTotalPages(), photos.isLast());

	}

	public ResponseEntity<?> getPhoto(Long id) {
		Photo photo = photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo", "id", id));
		return new ResponseEntity<>(new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
				photo.getThumbnailUrl(), photo.getAlbum().getId()), HttpStatus.OK);
	}

	public ResponseEntity<?> updatePhoto(Long id, PhotoRequest photoRequest, UserPrincipal currentUser) {
		Album album = albumRepository.findById(photoRequest.getAlbumId())
				.orElseThrow(() -> new ResourceNotFoundException("Album", "id", photoRequest.getAlbumId()));
		Photo photo = photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo", "id", id));
		if (photo.getAlbum().getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			photo.setTitle(photoRequest.getTitle());
			photo.setThumbnailUrl(photoRequest.getThumbnailUrl());
			photo.setAlbum(album);
			Photo updatedPhoto = photoRepository.save(photo);
			return new ResponseEntity<>(new PhotoResponse(updatedPhoto.getId(), updatedPhoto.getTitle(),
					updatedPhoto.getUrl(), updatedPhoto.getThumbnailUrl(), updatedPhoto.getAlbum().getId()),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update this photo"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> addPhoto(PhotoRequest photoRequest, UserPrincipal currentUser) {
		Album album = albumRepository.findById(photoRequest.getAlbumId())
				.orElseThrow(() -> new ResourceNotFoundException("Album", "id", photoRequest.getAlbumId()));
		if (album.getUser().getId().equals(currentUser.getId())) {
			Photo photo = new Photo(photoRequest.getTitle(), photoRequest.getUrl(), photoRequest.getThumbnailUrl(),
					album);
			Photo newPhoto = photoRepository.save(photo);
			return new ResponseEntity<>(new PhotoResponse(newPhoto.getId(), newPhoto.getTitle(), newPhoto.getUrl(),
					newPhoto.getThumbnailUrl(), newPhoto.getAlbum().getId()), HttpStatus.CREATED);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to add photo in this album"),
				HttpStatus.UNAUTHORIZED);
	}

	public ResponseEntity<?> deletePhoto(Long id, UserPrincipal currentUser) {
		Photo photo = photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo", "id", id));
		if (photo.getAlbum().getUser().getId().equals(currentUser.getId())
				|| currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
			photoRepository.deleteById(id);
			return new ResponseEntity<>(new ApiResponse(true, "Photo deleted successfully"), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to delete this photo"),
				HttpStatus.UNAUTHORIZED);
	}

	public PagedResponse<?> getAllPhotosByAlbum(Long albumId, int page, int size) {
		validatePageNumberAndSize(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

		Page<Photo> photos = photoRepository.findByAlbumId(albumId, pageable);

		List<PhotoResponse> photoResponses = new ArrayList<>();
		for (Photo photo : photos.getContent()) {
			photoResponses.add(new PhotoResponse(photo.getId(), photo.getTitle(), photo.getUrl(),
					photo.getThumbnailUrl(), photo.getAlbum().getId()));
		}

		return new PagedResponse<>(photoResponses, photos.getNumber(), photos.getSize(), photos.getTotalElements(),
				photos.getTotalPages(), photos.isLast());
	}

	private void validatePageNumberAndSize(int page, int size) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be less than zero.");
		}

		if (size < 0) {
			throw new BadRequestException("Size number cannot be less than zero.");
		}

		if (size > AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
		}
	}
}
