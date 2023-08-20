package com.sopromadze.blogapi.infrastructure.rest.controller;

import com.sopromadze.blogapi.application.TagUseCase;
import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.infrastructure.rest.dto.request.TagRequestDto;
import com.sopromadze.blogapi.infrastructure.rest.dto.response.TagResponseDto;
import com.sopromadze.blogapi.infrastructure.rest.mapper.TagMapper;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TagService;
import com.sopromadze.blogapi.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;
    private final TagUseCase tagUseCase;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<PagedResponse<TagEntity>> getAllTags(
            @RequestParam(name = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {

        PagedResponse<TagEntity> response = tagService.getAllTags(page, size);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagRequestDto tagRequestDto) {

        Tag createdTag = tagUseCase.createTag(tagMapper.toDomain(tagRequestDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagMapper.toResponseDto(createdTag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagEntity> getTag(@PathVariable(name = "id") Long id) {
        TagEntity tag = tagService.getTag(id);

        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TagEntity> updateTag(@PathVariable(name = "id") Long id, @Valid @RequestBody TagEntity tag, @CurrentUser UserPrincipal currentUser) {

        TagEntity updatedTag = tagService.updateTag(id, tag, currentUser);

        return new ResponseEntity<>(updatedTag, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteTag(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = tagService.deleteTag(id, currentUser);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
