package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
	Page<Album> findByUser_Id(Long userId, Pageable pageable);
}
