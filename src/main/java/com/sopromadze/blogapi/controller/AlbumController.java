package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    private final AlbumService albumService;
    private final PhotoService photoService;

    @Autowired
    public AlbumController(AlbumService albumService, PhotoService photoService) {
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @GetMapping
    public PagedResponse<Album> getAllAlbums(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return albumService.getAllAlbums(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addAlbum(@Valid @RequestBody Album album, @CurrentUser UserPrincipal currentUser){
        return albumService.addAlbum(album, currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbum(@PathVariable(name = "id") Long id){
        return albumService.getAlbum(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateAlbum(@PathVariable(name = "id") Long id, @Valid @RequestBody Album newAlbum, @CurrentUser UserPrincipal currentUser){
        return albumService.updateAlbum(id, newAlbum, currentUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAlbum(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser){
        return albumService.deleteAlbum(id, currentUser);
    }

    @GetMapping("/{id}/photos")
    public PagedResponse<?> getAllPhotosByAlbum(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return photoService.getAllPhotosByAlbum(id, page, size);
    }

}
