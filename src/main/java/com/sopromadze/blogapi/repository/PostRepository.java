package com.sopromadze.blogapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.sopromadze.blogapi.model.post.Post;
import com.sopromadze.blogapi.model.tag.Tag;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Page<Post> findByCreatedBy(Long userId, Pageable pageable);
    Page<Post> findByCategory(Long categoryId, Pageable pageable);
    Page<Post> findByTags(List<Tag> tags, Pageable pageable);
    Long countByCreatedBy(Long userId);
}
