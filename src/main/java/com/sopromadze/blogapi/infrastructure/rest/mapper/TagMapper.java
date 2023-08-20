package com.sopromadze.blogapi.infrastructure.rest.mapper;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.infrastructure.rest.dto.request.TagRequestDto;
import com.sopromadze.blogapi.infrastructure.rest.dto.response.TagResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toDomain(TagRequestDto tagRequestDto);

    TagResponseDto toResponseDto(Tag tag);
}
