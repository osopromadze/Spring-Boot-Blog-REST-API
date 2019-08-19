package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.service.UserService;
import com.sopromadze.blogapi.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final AlbumService albumService;

    @Autowired
    public UserController(UserService userService, PostService postService, AlbumService albumService) {
        this.userService = userService;
        this.postService = postService;
        this.albumService = albumService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return userService.getCurrentUser(currentUser);
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username){
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email){
        return userService.checkEmailAvailability(email);
    }

    @GetMapping("/{username}/profile")
    public UserProfile getUSerProfile(@PathVariable(value = "username") String username){
        return userService.getUserProfile(username);
    }

    @GetMapping("/{username}/posts")
    public PagedResponse<Post> getPostsCreatedBy(
            @PathVariable(value = "username") String username,
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){
        return postService.getPostsByCreatedBy(username, page, size);
    }

    @GetMapping("/{username}/albums")
    public PagedResponse<Album> getUserAlbums(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size){

        return albumService.getUserAlbums(username, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User newUser, @PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.updateUser(newUser, username, currentUser);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.deleteUser(username, currentUser);
    }

    @PutMapping("/{username}/giveAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> giveAdmin(@PathVariable(name = "username") String username){
        return userService.giveAdmin(username);
    }

    @PutMapping("/{username}/takeAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> takeAdmin(@PathVariable(name = "username") String username){
        return userService.takeAdmin(username);
    }

    @PutMapping("/setOrUpdateInfo")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> setAddress(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody InfoRequest infoRequest){
        return userService.setOrUpdateInfo(currentUser, infoRequest);
    }

}
