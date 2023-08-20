package com.sopromadze.blogapi.infrastructure.rest.mapper;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.infrastructure.rest.dto.request.TagRequestDto;
import com.sopromadze.blogapi.infrastructure.rest.dto.response.TagResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TagMapperTest {

    private final TagMapper tagMapper = Mappers.getMapper(TagMapper.class);

    @Test
    void toDomain_OK() {
        TagRequestDto tagRequestDto = TagRequestDto.builder().name("TEST").build();
        Tag tag = tagMapper.toDomain(tagRequestDto);

        assertThat(tag.getName()).isEqualTo(tagRequestDto.getName());
    }

    @Test
    void toResponseDto_OK() {
        Tag tag = TagBuilder.build();
        TagResponseDto responseDto = tagMapper.toResponseDto(tag);

        assertThat(responseDto).usingRecursiveComparison().isEqualTo(tag);
    }
}