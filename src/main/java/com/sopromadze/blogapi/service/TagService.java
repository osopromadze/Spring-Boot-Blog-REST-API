package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.post.Tag;
import com.sopromadze.blogapi.payload.response.ApiResponse;
import com.sopromadze.blogapi.payload.response.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface TagService {

	PagedResponse<Tag> getAllTags(int page, int size);

	Tag getTag(Long id);

	Tag addTag(Tag tag, UserPrincipal currentUser);

	Tag updateTag(Long id, Tag newTag, UserPrincipal currentUser);

	ApiResponse deleteTag(Long id, UserPrincipal currentUser);

}
