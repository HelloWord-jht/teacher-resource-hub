package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.ResourceSourceAuditRequest;
import com.teacherresourcehub.dto.ResourceSourceSaveRequest;
import com.teacherresourcehub.vo.ResourceSourceVO;

import java.util.List;

public interface ResourceSourceService {

    List<ResourceSourceVO> list(String authorizationStatus);

    ResourceSourceVO getById(Long id);

    void create(ResourceSourceSaveRequest request);

    void update(Long id, ResourceSourceSaveRequest request);

    void audit(Long id, ResourceSourceAuditRequest request);

    long countPending();

    long countRisk();
}
