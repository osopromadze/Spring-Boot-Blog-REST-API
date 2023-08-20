package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface TagService {

    PagedResponse<TagEntity> getAllTags(int page, int size);

    TagEntity getTag(Long id);

    TagEntity updateTag(Long id, TagEntity newTag, UserPrincipal currentUser);

    ApiResponse deleteTag(Long id, UserPrincipal currentUser);

}
