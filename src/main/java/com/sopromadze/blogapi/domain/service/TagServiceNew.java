package com.sopromadze.blogapi.domain.service;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.port.TagPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagServiceNew {
    private final TagPersistencePort tagPersistencePort;

    public Tag createTag(Tag tag) {
        return tagPersistencePort.createTag(tag);
    }
}
