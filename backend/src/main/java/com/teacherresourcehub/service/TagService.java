package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.TagSaveRequest;
import com.teacherresourcehub.vo.TagVO;

import java.util.List;

public interface TagService {

    List<TagVO> listEnabledTags();

    List<TagVO> listAdminTags();

    List<TagVO> listByIds(List<Long> ids);

    void createTag(TagSaveRequest request);

    void updateTag(Long id, TagSaveRequest request);

    void updateTagStatus(Long id, Integer status);

    void deleteTag(Long id);

    long countTotal();
}
