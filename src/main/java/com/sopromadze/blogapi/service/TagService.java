package com.sopromadze.blogapi.service;

import org.springframework.http.ResponseEntity;

import com.sopromadze.blogapi.model.tag.Tag;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;

public interface TagService {

	PagedResponse<Tag> getAllTags(int page, int size);

	ResponseEntity<?> getTag(Long id);

	ResponseEntity<?> addTag(Tag tag, UserPrincipal currentUser);

	ResponseEntity<?> updateTag(Long id, Tag newTag, UserPrincipal currentUser);

	ResponseEntity<?> deleteTag(Long id, UserPrincipal currentUser);

}