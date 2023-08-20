package com.sopromadze.blogapi.domain.port;

import com.sopromadze.blogapi.domain.model.Tag;

public interface TagPersistencePort {
    Tag createTag(Tag tag);
}
