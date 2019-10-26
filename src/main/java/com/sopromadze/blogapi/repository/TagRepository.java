package com.sopromadze.blogapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.tag.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag findByName(String name);
}
