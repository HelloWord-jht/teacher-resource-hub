package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.ResourceStorageSaveRequest;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceStorage;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourceStorageMapper;
import com.teacherresourcehub.service.ResourceStorageService;
import com.teacherresourcehub.vo.ResourceStorageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceStorageServiceImpl implements ResourceStorageService {

    private final ResourceStorageMapper resourceStorageMapper;
    private final ResourceMapper resourceMapper;

    public ResourceStorageServiceImpl(ResourceStorageMapper resourceStorageMapper, ResourceMapper resourceMapper) {
        this.resourceStorageMapper = resourceStorageMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public List<ResourceStorageVO> list(Long resourceId) {
        LambdaQueryWrapper<ResourceStorage> wrapper = new LambdaQueryWrapper<ResourceStorage>()
                .orderByDesc(ResourceStorage::getUpdatedAt, ResourceStorage::getId);
        if (resourceId != null) {
            wrapper.eq(ResourceStorage::getResourceId, resourceId);
        }
        List<ResourceStorage> list = resourceStorageMapper.selectList(wrapper);
        Map<Long, Resource> resourceMap = fetchResourceMap(list);
        return list.stream().map(item -> toVO(item, resourceMap.get(item.getResourceId()))).toList();
    }

    @Override
    public ResourceStorageVO getById(Long id) {
        ResourceStorage storage = getOrThrow(id);
        Resource resource = resourceMapper.selectById(storage.getResourceId());
        return toVO(storage, resource);
    }

    @Override
    public void create(ResourceStorageSaveRequest request) {
        ensureResourceExists(request.getResourceId());
        ResourceStorage storage = new ResourceStorage();
        BeanUtils.copyProperties(request, storage);
        resourceStorageMapper.insert(storage);
    }

    @Override
    public void update(Long id, ResourceStorageSaveRequest request) {
        ensureResourceExists(request.getResourceId());
        ResourceStorage storage = getOrThrow(id);
        BeanUtils.copyProperties(request, storage);
        resourceStorageMapper.updateById(storage);
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        resourceStorageMapper.deleteById(id);
    }

    private void ensureResourceExists(Long resourceId) {
        if (resourceMapper.selectById(resourceId) == null) {
            throw new BusinessException("资源不存在");
        }
    }

    private ResourceStorage getOrThrow(Long id) {
        ResourceStorage storage = resourceStorageMapper.selectById(id);
        if (storage == null) {
            throw new BusinessException("网盘绑定不存在");
        }
        return storage;
    }

    private Map<Long, Resource> fetchResourceMap(List<ResourceStorage> list) {
        List<Long> resourceIds = list.stream().map(ResourceStorage::getResourceId).distinct().toList();
        if (resourceIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return resourceMapper.selectList(new LambdaQueryWrapper<Resource>().in(Resource::getId, resourceIds))
                .stream()
                .collect(Collectors.toMap(Resource::getId, item -> item, (a, b) -> a));
    }

    private ResourceStorageVO toVO(ResourceStorage storage, Resource resource) {
        ResourceStorageVO vo = new ResourceStorageVO();
        BeanUtils.copyProperties(storage, vo);
        if (resource != null) {
            vo.setResourceCode(resource.getResourceCode());
            vo.setResourceTitle(resource.getTitle());
        }
        return vo;
    }
}
