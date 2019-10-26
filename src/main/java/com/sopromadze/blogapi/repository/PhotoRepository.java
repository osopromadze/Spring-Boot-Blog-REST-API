package com.sopromadze.blogapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.photo.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
	Page<Photo> findByAlbumId(Long albumId, Pageable pageable);
}
