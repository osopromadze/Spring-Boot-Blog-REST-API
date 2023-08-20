package com.sopromadze.blogapi.infrastructure.rest.controller;

import com.sopromadze.blogapi.application.TagUseCase;
import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.infrastructure.rest.dto.request.TagRequestDto;
import com.sopromadze.blogapi.infrastructure.rest.dto.response.TagResponseDto;
import com.sopromadze.blogapi.infrastructure.rest.mapper.TagMapper;
import com.sopromadze.blogapi.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    @Mock
    private TagUseCase tagUseCase;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    @Spy
    private TagController tagController;

    @Test
    void createTag_OK() {
        when(tagMapper.toDomain(any())).thenReturn(TagBuilder.build());
        Tag createdTag = TagBuilder.build();
        when(tagUseCase.createTag(any())).thenReturn(createdTag);
        when(tagMapper.toResponseDto(createdTag)).thenReturn(
                TagResponseDto.builder().tagId(createdTag.getTagId()).name(createdTag.getName()).build()
        );

        TagRequestDto tagRequestDto = TagRequestDto.builder().name("test").build();
        ResponseEntity<TagResponseDto> response = tagController.createTag(tagRequestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(createdTag);
    }
}