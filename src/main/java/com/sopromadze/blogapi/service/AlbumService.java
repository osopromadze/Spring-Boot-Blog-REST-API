package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AlbumService {
	@Autowired
    private AlbumRepository albumRepository;
	
	@Autowired
    private UserRepository userRepository;

    public PagedResponse<Album> getAllAlbums(int page, int size){
        validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Album> albums = albumRepository.findAll(pageable);

        if (albums.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(), albums.isLast());
        }
        return new PagedResponse<>(albums.getContent(), albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(), albums.isLast());
    }

    public ResponseEntity<?> addAlbum(Album album, UserPrincipal currentUser){
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
        album.setUser(user);
        Album newAlbum =  albumRepository.save(album);
        return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getAlbum(Long id){
        Album album = albumRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));
        return new ResponseEntity<>(album, HttpStatus.OK);
    }

    public ResponseEntity<?> updateAlbum(Long id, Album newAlbum, UserPrincipal currentUser){
        Album album = albumRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
        if (album.getUser().getId().equals(user.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            album.setTitle(newAlbum.getTitle());
            Album updatedAlbum = albumRepository.save(album);
            return new ResponseEntity<>(updatedAlbum, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteAlbum(Long id, UserPrincipal currentUser){
        Album album = albumRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Album", "id", id));
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUser.getUsername()));
        if (album.getUser().getId().equals(user.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            albumRepository.deleteById(id);
            return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted album"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to make this operation"), HttpStatus.UNAUTHORIZED);
    }

    public PagedResponse<Album> getUserAlbums(String username, int page, int size){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Album> albums = albumRepository.findByCreatedBy(user.getId(), pageable);

        if (albums.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(), albums.isLast());
        }
        return new PagedResponse<>(albums.getContent(), albums.getNumber(), albums.getSize(), albums.getTotalElements(), albums.getTotalPages(), albums.isLast());
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 0) {
            throw new BadRequestException("Size number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
