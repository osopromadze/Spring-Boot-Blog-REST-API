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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "5- Albums", description = "Operations related to albums")

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

    @Operation(description = "Get all albums", summary = "Get albums")
    @GetMapping
    public PagedResponse<AlbumResponse> getAllAlbums(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        return albumService.getAllAlbums(page, size);
    }

    @Operation(description = "Create new album (By logged in user)", summary = "Create album")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Album> addAlbum(@Valid @RequestBody AlbumRequest albumRequest, @CurrentUser UserPrincipal currentUser) {
        return albumService.addAlbum(albumRequest, currentUser);
    }

    @Operation(description = "Get album by id", summary = "Get album")
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable(name = "id") Long id) {
        return albumService.getAlbum(id);
    }

    @Operation(description = "Update album (If album belongs to logged in user or logged in user is admin)", summary = "Update album")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable(name = "id") Long id, @Valid @RequestBody AlbumRequest newAlbum,
                                                     @CurrentUser UserPrincipal currentUser) {
        return albumService.updateAlbum(id, newAlbum, currentUser);
    }

    @Operation(description = "Delete album (If album belongs to logged in user or logged in user is admin)", summary = "Delete album")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteAlbum(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        return albumService.deleteAlbum(id, currentUser);
    }

    @Operation(description = "Get all photos which belongs to album with id = id", summary = "Get photos")
    @GetMapping("/{id}/photos")
    public ResponseEntity<PagedResponse<PhotoResponse>> getAllPhotosByAlbum(@PathVariable(name = "id") Long id,
                                                                            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                                            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        PagedResponse<PhotoResponse> response = photoService.getAllPhotosByAlbum(id, page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
