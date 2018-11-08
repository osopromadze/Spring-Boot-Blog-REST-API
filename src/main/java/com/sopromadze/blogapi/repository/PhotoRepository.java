package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.photo.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends PagingAndSortingRepository<Photo, Long> {
    Page<Photo> findByAlbumId(Long albumId, Pageable pageable);
}
