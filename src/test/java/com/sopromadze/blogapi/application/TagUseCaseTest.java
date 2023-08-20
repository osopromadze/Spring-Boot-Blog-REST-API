package com.sopromadze.blogapi.application;

import com.sopromadze.blogapi.domain.model.Tag;
import com.sopromadze.blogapi.domain.model.TagBuilder;
import com.sopromadze.blogapi.domain.service.TagServiceNew;
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
class TagUseCaseTest {

    @Mock
    private TagServiceNew tagService;

    @InjectMocks
    @Spy
    private TagUseCase tagUseCase;

    @Test
    void createTag_OK() {
        Tag tag = TagBuilder.build();
        when(tagService.createTag(any())).thenReturn(tag);

        Tag response = tagUseCase.createTag(TagBuilder.build());

        assertThat(response).isEqualTo(tag);

        verify(tagService, times(1)).createTag(any());
    }
}