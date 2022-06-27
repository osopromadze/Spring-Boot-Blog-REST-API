package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.exception.ResponseEntityErrorException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.utils.AppConstants;
import com.sopromadze.blogapi.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
	@Autowired
	private AlbumService albumService;

	@Autowired
	private PhotoService photoService;

	@ExceptionHandler(ResponseEntityErrorException.class)
	public ResponseEntity<ApiResponse> handleExceptions(ResponseEntityErrorException exception) {
		return exception.getApiResponse();
	}

	@GetMapping
	public PagedResponse<AlbumResponse> getAllAlbums(
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
		AppUtils.validatePageNumberAndSize(page, size);

		return albumService.getAllAlbums(page, size);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Album> addAlbum(@Valid @RequestBody AlbumRequest albumRequest, @CurrentUser UserPrincipal currentUser) {
		return albumService.addAlbum(albumRequest, currentUser);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Album> getAlbum(@PathVariable(name = "id") Long id) {
		return albumService.getAlbum(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable(name = "id") Long id, @Valid @RequestBody AlbumRequest newAlbum,
			@CurrentUser UserPrincipal currentUser) {
		return albumService.updateAlbum(id, newAlbum, currentUser);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<ApiResponse> deleteAlbum(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
		return albumService.deleteAlbum(id, currentUser);
	}

	@GetMapping("/{id}/photos")
	public ResponseEntity<PagedResponse<PhotoResponse>> getAllPhotosByAlbum(@PathVariable(name = "id") Long id,
			@RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
			@RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

		PagedResponse<PhotoResponse> response = photoService.getAllPhotosByAlbum(id, page, size);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
