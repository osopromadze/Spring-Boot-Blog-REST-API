package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.tag.Tag;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.TagRequest;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface TagService {

	PagedResponse<Tag> getAllTags(int page, int size);

	Tag getTag(Long id);

	Tag addTag(TagRequest tagRequest, UserPrincipal currentUser);

	Tag updateTag(Long id, TagRequest tagRequest, UserPrincipal currentUser);

	ApiResponse deleteTag(Long id, UserPrincipal currentUser);

}
