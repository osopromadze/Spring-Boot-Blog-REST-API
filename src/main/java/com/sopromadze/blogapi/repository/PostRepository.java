package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

//@Repository
public interface PostRepository {
    Page<Post> findByCreatedBy(Long userId, Pageable pageable);

    Page<Post> findByCategory(Long categoryId, Pageable pageable);

    Page<Post> findByTags(List<TagEntity> tags, Pageable pageable);

    Long countByCreatedBy(Long userId);

    Optional<Post> findById(Long postId);

    Page<Post> findAll(Pageable pageable);

    Post save(Post post);

    void deleteById(Long id);

}
