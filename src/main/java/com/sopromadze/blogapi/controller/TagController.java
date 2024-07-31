package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TagService;
import com.sopromadze.blogapi.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "8- Tags", description = "Operations related to tags")
@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @Operation(description = "Get all tags", summary = "Get tags")
    @GetMapping
    public ResponseEntity<PagedResponse<Tag>> getAllTags(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        PagedResponse<Tag> response = tagService.getAllTags(page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(description = "Create tag (Only for logged in user)", summary = "Create tag")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Tag> addTag(@Valid @RequestBody Tag tag, @CurrentUser UserPrincipal currentUser) {
        Tag newTag = tagService.addTag(tag, currentUser);

        return new ResponseEntity<>(newTag, HttpStatus.CREATED);
    }

    @Operation(description = "Get tag", summary = "Get tag")
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable(name = "id") Long id) {
        Tag tag = tagService.getTag(id);

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @Operation(description = "Update tag (only for logged in user or admin", summary = "Update tag")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Tag> updateTag(@PathVariable(name = "id") Long id, @Valid @RequestBody Tag tag, @CurrentUser UserPrincipal currentUser) {

        Tag updatedTag = tagService.updateTag(id, tag, currentUser);

        return new ResponseEntity<>(updatedTag, HttpStatus.OK);
    }

    @Operation(description = "Delete tag (Only for logged in user or admin", summary = "Delete tag")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteTag(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = tagService.deleteTag(id, currentUser);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
