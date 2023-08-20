package com.sopromadze.blogapi.infrastructure.persistence.mysql.repository;

import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagMysqlRepository extends JpaRepository<TagEntity, Long> {
    TagEntity findByName(String name);
}
