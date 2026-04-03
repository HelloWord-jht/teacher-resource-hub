package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.ResourceStorageSaveRequest;
import com.teacherresourcehub.vo.ResourceStorageVO;

import java.util.List;

public interface ResourceStorageService {

    List<ResourceStorageVO> list(Long resourceId);

    ResourceStorageVO getById(Long id);

    void create(ResourceStorageSaveRequest request);

    void update(Long id, ResourceStorageSaveRequest request);

    void delete(Long id);
}
