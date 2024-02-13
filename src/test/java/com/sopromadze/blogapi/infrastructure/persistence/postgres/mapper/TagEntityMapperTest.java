package com.sopromadze.blogapi.infrastructure.persistence.postgres.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.infrastructure.persistence.postgres.entity.TagEntity;
import com.sopromadze.blogapi.infrastructure.persistence.postgres.entity.TagEntityBuilder;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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