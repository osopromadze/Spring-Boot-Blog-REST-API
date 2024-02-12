package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.entity.TagEntity;
import com.sopromadze.blogapi.infrastructure.persistence.mysql.repository.TagMysqlRepository;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TagService;
import com.sopromadze.blogapi.utils.AppUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

//    @Autowired
    private TagMysqlRepository tagMysqlRepository;

    @Override
    public PagedResponse<TagEntity> getAllTags(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<TagEntity> tags = tagMysqlRepository.findAll(pageable);

        List<TagEntity> content = tags.getNumberOfElements() == 0 ? Collections.emptyList() : tags.getContent();

        return new PagedResponse<>(content, tags.getNumber(), tags.getSize(), tags.getTotalElements(), tags.getTotalPages(), tags.isLast());
    }

    @Override
    public TagEntity getTag(Long id) {
        return tagMysqlRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }

    @Override
    public TagEntity updateTag(Long id, TagEntity newTag, UserPrincipal currentUser) {
        TagEntity tag = tagMysqlRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        if (tag.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            tag.setName(newTag.getName());
            return tagMysqlRepository.save(tag);
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this tag");

        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public ApiResponse deleteTag(Long id, UserPrincipal currentUser) {
        TagEntity tag = tagMysqlRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        if (tag.getCreatedBy().equals(currentUser.getId()) || currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            tagMysqlRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted tag");
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this tag");

        throw new UnauthorizedException(apiResponse);
    }
}






















