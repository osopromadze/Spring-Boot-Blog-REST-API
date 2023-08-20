package com.sopromadze.blogapi.infrastructure.persistence.mysql.mapper;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntityBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class TagEntityMapperTest {

    private final TagEntityMapper tagEntityMapper = Mappers.getMapper(TagEntityMapper.class);

    @Test
    void toEntity_OK() {
        Tag tag = TagBuilder.build();
        TagEntity entity = tagEntityMapper.toEntity(tag);

        assertThat(entity.getId()).hasToString(tag.getTagId());
        assertThat(entity.getName()).isEqualTo(tag.getName());
    }

    @Test
    void toDomain_OK() {
        TagEntity entity = TagEntityBuilder.build();
        Tag tag = tagEntityMapper.toDomain(entity);

        assertThat(tag.getTagId()).isEqualTo(entity.getId().toString());
        assertThat(tag.getName()).isEqualTo(entity.getName());
    }
}