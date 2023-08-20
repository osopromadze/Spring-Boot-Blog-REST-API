package com.sopromadze.blogapi.infrastructure.persistence.mysql.mapper;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagEntityMapper {

    @Mapping(source = "tagId", target = "id")
    TagEntity toEntity(Tag tag);

    @Mapping(source = "id", target = "tagId")
    Tag toDomain(TagEntity tagEntity);
}
