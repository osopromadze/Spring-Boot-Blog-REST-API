package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Page<Post> findByCreatedBy(Long userId, Pageable pageable);
    Long countByCreatedBy(Long userId);
}
