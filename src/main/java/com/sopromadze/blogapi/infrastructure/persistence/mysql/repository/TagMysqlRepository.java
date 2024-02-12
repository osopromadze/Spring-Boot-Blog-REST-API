package com.sopromadze.blogapi.infrastructure.persistence.mysql.repository;

import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface TagMysqlRepository {
    TagEntity findByName(String name);

    TagEntity save(TagEntity entity);

    Page<TagEntity> findAll(Pageable pageable);

    Optional<TagEntity> findById(Long id);

    void deleteById(Long id);

}
