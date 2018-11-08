package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.album.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album, Long> {
    Page<Album> findByCreatedBy(Long userId, Pageable pageable);
}
