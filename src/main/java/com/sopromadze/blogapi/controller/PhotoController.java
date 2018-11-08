package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.photo.Photo;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public PagedResponse<PhotoResponse> getAllPhotos(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return photoService.getAllPhotos(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPhoto(@Valid @RequestBody PhotoRequest photoRequest, @CurrentUser UserPrincipal currentUser){
        return photoService.addPhoto(photoRequest, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPhoto(@PathVariable(name = "id") Long id){
        return photoService.getPhoto(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePhoto(@PathVariable(name = "id") Long id, @Valid @RequestBody PhotoRequest photoRequest, @CurrentUser UserPrincipal currentUser){
        return photoService.updatePhoto(id, photoRequest, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deletePhoto(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return photoService.deletePhoto(id, currentUser);
    }
}
