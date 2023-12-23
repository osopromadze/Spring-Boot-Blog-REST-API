package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
public class TagServiceImplTests {

    @Mock
    private AppUtils appUtils;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    public void getAllTags_whenNoTagsFound_thenReturnEmptyList() {
        Mockito.when(tagRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        PagedResponse<Tag> response = tagService.getAllTags(1, 2);

        Assert.assertEquals(0, response.getContent().size());
        Assert.assertEquals(Collections.emptyList(), response.getContent());
    }

    @Test
    public void getAllTags_whenTagsFound_thenReturnContent() {
        Page<Tag> page = Mockito.mock(Page.class);

        Mockito.when(page.getTotalPages()).thenReturn(1);
        Mockito.when(page.getTotalElements()).thenReturn(1L);
        Mockito.when(page.getNumber()).thenReturn(0);
        Mockito.when(page.getSize()).thenReturn(1);
        Mockito.when(page.getNumberOfElements()).thenReturn(1);
        Mockito.when(page.getContent()).thenReturn(createTagList());

        Mockito.when(tagRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        PagedResponse<Tag> response = tagService.getAllTags(1, 2);

        Assert.assertNotNull(response.getContent());
        Assert.assertEquals(1, response.getContent().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getTag_whenTagNotFound_thenThrowException() {
        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        tagService.getTag(1L);
    }

    @Test
    public void getTag_whenTagFound_thenReturnTag() {
        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Tag()));

        tagService.getTag(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateTag_whenTagNotFound_thenThrowException() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        tagService.updateTag(1L, new Tag(), fakeUserPrincipal);
    }

    @Test
    public void updateTag_whenUserMatches_thenSaveTag() {
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Tag tag = new Tag();
        tag.setName("newName");
        tag.setCreatedBy(1L);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        tagService.updateTag(1L, tag, fakeUserPrincipal);
    }

    @Test
    public void updateTag_whenUserAdmin_thenSaveTag() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Tag tag = new Tag();
        tag.setName("newName");
        tag.setCreatedBy(2L);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        tagService.updateTag(1L, tag, fakeUserPrincipal);
    }

    @Test(expected = UnauthorizedException.class)
    public void updateTag_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Tag tag = new Tag();
            tag.setName("newName");
            tag.setCreatedBy(2L);

            Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

            tagService.updateTag(1L, tag, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void deleteTag_whenTagNotFound_thenThrowException() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        tagService.deleteTag(1L, fakeUserPrincipal);
    }

    @Test
    public void deleteTag_whenUserAuthorized_thenDeleteTag() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Tag tag = new Tag();
        tag.setName("newName");
        tag.setCreatedBy(1L);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        ApiResponse response = tagService.deleteTag(1L, fakeUserPrincipal);

        Assert.assertNotNull(response.getMessage());
        Assert.assertTrue(response.getSuccess());
    }

    @Test
    public void deleteTag_whenUserAdmin_thenDeleteTag() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()));
        UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                "john.doe@example.com", "password123", authorities);
        Tag tag = new Tag();
        tag.setName("newName");
        tag.setCreatedBy(2L);

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

        ApiResponse response = tagService.deleteTag(1L, fakeUserPrincipal);

        Assert.assertNotNull(response.getMessage());
        Assert.assertTrue(response.getSuccess());
    }

    @Test(expected = UnauthorizedException.class)
    public void deleteTag_whenUserNotAuthorized_thenThrowException() {
        try {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserPrincipal fakeUserPrincipal = new UserPrincipal(1L, "John", "Doe", "john.doe",
                    "john.doe@example.com", "password123", authorities);
            Tag tag = new Tag();
            tag.setName("newName");
            tag.setCreatedBy(2L);

            Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));

            tagService.deleteTag(1L, fakeUserPrincipal);
        } catch (UnauthorizedException e) {
            Assert.assertNotNull(e.getApiResponse().getMessage());
            Assert.assertFalse(e.getApiResponse().getSuccess());
            throw e;
        }
    }

    private List<Tag> createTagList() {
        List<Tag> list = new ArrayList<>();
        Tag tag = new Tag();
        list.add(tag);
        return list;
    }
}
