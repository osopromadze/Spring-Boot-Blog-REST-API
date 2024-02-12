package com.sopromadze.blogapi.infrastructure.persistence.mysql.adapter;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.port.TagPersistencePort;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.mapper.TagEntityMapper;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.repository.TagMysqlRepository;
import org.springframework.stereotype.Service;

//@RequiredArgsConstructor
@Service
public class TagPersistenceMysqlAdapter implements TagPersistencePort {

    private TagMysqlRepository tagMysqlRepository;
    private TagEntityMapper tagEntityMapper;

    @Override
    public Tag createTag(Tag tag) {
        TagEntity savedTag = tagMysqlRepository.save(tagEntityMapper.toEntity(tag));
        return tagEntityMapper.toDomain(savedTag);
    }
}
