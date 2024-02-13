package com.sopromadze.blogapi.infrastructure.persistence.postgres.repository;

import java.util.Optional;

import com.sopromadze.blogapi.infrastructure.persistence.postgres.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//@Repository
public interface TagMysqlRepository {

  TagEntity findByName(String name);

  TagEntity save(TagEntity entity);

  Page<TagEntity> findAll(Pageable pageable);

  Optional<TagEntity> findById(Long id);

  void deleteById(Long id);

}
