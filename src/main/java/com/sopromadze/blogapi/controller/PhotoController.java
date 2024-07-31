package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import javax.validation.Valid;

@Tag(name = "6- Photos", description = "Operations related to photos")
@RestController
@RequestMapping("/api/photos")
public class PhotoController {
	@Autowired
	private PhotoService photoService;

	@Operation(description = "Get all photos", summary = "Get photos")
	@GetMapping
	public PagedResponse<PhotoResponse> getAllPhotos(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		return photoService.getAllPhotos(page, size);
	}

	@Operation(description = "Create new photo (By logged in user)", summary = "Create photo")
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<PhotoResponse> addPhoto(@Valid @RequestBody PhotoRequest photoRequest,
			@CurrentUser UserPrincipal currentUser) {
		PhotoResponse photoResponse = photoService.addPhoto(photoRequest, currentUser);

		return new ResponseEntity< >(photoResponse, HttpStatus.OK);
	}

	@Operation(description = "Get photo by id", summary = "Get photo")
	@GetMapping("/{id}")
	public ResponseEntity<PhotoResponse> getPhoto(@PathVariable(name = "id") Long id) {
		PhotoResponse photoResponse = photoService.getPhoto(id);

		return new ResponseEntity< >(photoResponse, HttpStatus.OK);
	}

	@Operation(description = "Update photo (If photo belongs to logged in user or logged in user is admin", summary = "Update photo")
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<PhotoResponse> updatePhoto(@PathVariable(name = "id") Long id,
			@Valid @RequestBody PhotoRequest photoRequest, @CurrentUser UserPrincipal currentUser) {

		PhotoResponse photoResponse = photoService.updatePhoto(id, photoRequest, currentUser);

		return new ResponseEntity< >(photoResponse, HttpStatus.OK);
	}

	@Operation(description = "Delete photo (If photo belongs to logged in user or logged in user is admin)", summary = "Delete photo")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deletePhoto(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		ApiResponse apiResponse = photoService.deletePhoto(id, currentUser);

		return new ResponseEntity< >(apiResponse, HttpStatus.OK);
	}
}
