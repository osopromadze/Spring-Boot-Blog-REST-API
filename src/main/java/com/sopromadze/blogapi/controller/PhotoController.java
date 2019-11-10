package com.sopromadze.blogapi.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.utils.AppConstants;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
	@Autowired
	private PhotoService photoService;

	@GetMapping
	public PagedResponse<PhotoResponse> getAllPhotos(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return photoService.getAllPhotos(page, size);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> addPhoto(@Valid @RequestBody PhotoRequest photoRequest,
			@CurrentUser UserPrincipal currentUser) {
		return photoService.addPhoto(photoRequest, currentUser);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getPhoto(@PathVariable(name = "id") Long id) {
		return photoService.getPhoto(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updatePhoto(@PathVariable(name = "id") Long id,
			@Valid @RequestBody PhotoRequest photoRequest, @CurrentUser UserPrincipal currentUser) {
		return photoService.updatePhoto(id, photoRequest, currentUser);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> deletePhoto(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		return photoService.deletePhoto(id, currentUser);
	}
}
