package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

//@Repository
public interface AlbumRepository  {
	Page<Album> findByCreatedBy(Long userId, Pageable pageable);

	Optional<Album> findById(Long albumId);

	Page<Album> findAll(Pageable pageable);

	Album save(Album album);

	void deleteById(Long id);

}
