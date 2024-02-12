package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface PhotoRepository {
	Page<Photo> findByAlbumId(Long albumId, Pageable pageable);

	Page<Photo> findAll(Pageable pageable);

	Optional<Photo> findById(Long id);

	Photo save(Photo photo);

	void deleteById(Long id);

}
