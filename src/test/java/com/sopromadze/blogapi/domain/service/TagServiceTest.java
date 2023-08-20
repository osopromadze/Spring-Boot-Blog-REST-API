package com.sopromadze.blogapi.domain.service;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.domain.port.TagPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagPersistencePort tagPersistencePort;

    @InjectMocks
    @Spy
    private TagServiceNew tagService;

    @Test
    void createTag_OK() {
        Tag tag = TagBuilder.build();
        when(tagPersistencePort.createTag(any())).thenReturn(tag);

        Tag response = tagService.createTag(TagBuilder.build());

        assertThat(response).isEqualTo(tag);

        verify(tagPersistencePort, times(1)).createTag(any());
    }

}