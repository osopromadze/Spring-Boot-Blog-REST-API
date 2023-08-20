package com.sopromadze.blogapi.application;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.service.TagServiceNew;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagUseCase {
    private final TagServiceNew tagService;

    public Tag createTag(Tag tag) {
        return tagService.createTag(tag);
    }
}
