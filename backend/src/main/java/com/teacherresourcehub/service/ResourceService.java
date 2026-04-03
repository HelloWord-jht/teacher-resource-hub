package com.teacherresourcehub.service;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.ResourceAdminQueryRequest;
import com.teacherresourcehub.dto.ResourceQueryRequest;
import com.teacherresourcehub.dto.ResourceSaveRequest;
import com.teacherresourcehub.vo.AdminResourceListItemVO;
import com.teacherresourcehub.vo.ResourceAdminDetailVO;
import com.teacherresourcehub.vo.ResourceDetailVO;
import com.teacherresourcehub.vo.ResourceListItemVO;
import com.teacherresourcehub.vo.ResourceRelationVO;

import java.util.List;

public interface ResourceService {

    PageResult<ResourceListItemVO> pageWebResources(ResourceQueryRequest request);

    ResourceDetailVO getWebResourceDetail(Long id);

    ResourceDetailVO getWebResourceDetailByCode(String resourceCode);

    List<ResourceListItemVO> listPublishedResourceListItemsByIds(List<Long> ids);

    List<ResourceRelationVO> listPublishedResourceRelationsByIds(List<Long> ids);

    PageResult<AdminResourceListItemVO> pageAdminResources(ResourceAdminQueryRequest request);

    ResourceAdminDetailVO getAdminResourceDetail(Long id);

    ResourceAdminDetailVO getAdminResourceDetailByCode(String resourceCode);

    void createResource(ResourceSaveRequest request);

    void updateResource(Long id, ResourceSaveRequest request);

    void updateResourceStatus(Long id, Integer status);

    void deleteResource(Long id);

    long countTotal();
}
