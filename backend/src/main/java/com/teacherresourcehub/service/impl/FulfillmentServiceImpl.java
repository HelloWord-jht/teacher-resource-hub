package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.util.FulfillmentTemplateBuilder;
import com.teacherresourcehub.dto.FulfillmentMarkDeliveredRequest;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.DeliveryRecord;
import com.teacherresourcehub.entity.Lead;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceSearchLog;
import com.teacherresourcehub.entity.ResourceStorage;
import com.teacherresourcehub.entity.ResourceTag;
import com.teacherresourcehub.entity.Tag;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.DeliveryRecordMapper;
import com.teacherresourcehub.mapper.LeadMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourceSearchLogMapper;
import com.teacherresourcehub.mapper.ResourceStorageMapper;
import com.teacherresourcehub.mapper.ResourceTagMapper;
import com.teacherresourcehub.mapper.TagMapper;
import com.teacherresourcehub.security.AdminLoginUser;
import com.teacherresourcehub.service.FulfillmentService;
import com.teacherresourcehub.vo.DeliveryRecordVO;
import com.teacherresourcehub.vo.FulfillmentQuickSearchItemVO;
import com.teacherresourcehub.vo.FulfillmentResourceVO;
import com.teacherresourcehub.vo.ResourceSearchLogVO;
import com.teacherresourcehub.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FulfillmentServiceImpl implements FulfillmentService {

    private final ResourceMapper resourceMapper;
    private final ResourceStorageMapper resourceStorageMapper;
    private final ResourceSearchLogMapper resourceSearchLogMapper;
    private final DeliveryRecordMapper deliveryRecordMapper;
    private final CategoryMapper categoryMapper;
    private final ResourceTagMapper resourceTagMapper;
    private final TagMapper tagMapper;
    private final LeadMapper leadMapper;

    public FulfillmentServiceImpl(ResourceMapper resourceMapper,
                                  ResourceStorageMapper resourceStorageMapper,
                                  ResourceSearchLogMapper resourceSearchLogMapper,
                                  DeliveryRecordMapper deliveryRecordMapper,
                                  CategoryMapper categoryMapper,
                                  ResourceTagMapper resourceTagMapper,
                                  TagMapper tagMapper,
                                  LeadMapper leadMapper) {
        this.resourceMapper = resourceMapper;
        this.resourceStorageMapper = resourceStorageMapper;
        this.resourceSearchLogMapper = resourceSearchLogMapper;
        this.deliveryRecordMapper = deliveryRecordMapper;
        this.categoryMapper = categoryMapper;
        this.resourceTagMapper = resourceTagMapper;
        this.tagMapper = tagMapper;
        this.leadMapper = leadMapper;
    }

    @Override
    public List<FulfillmentQuickSearchItemVO> quickSearch(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        String trimmedKeyword = keyword.trim();
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<Resource>()
                .and(w -> w.eq(Resource::getResourceCode, trimmedKeyword)
                        .or()
                        .eq(Resource::getSlug, trimmedKeyword)
                        .or()
                        .like(Resource::getTitle, trimmedKeyword)
                        .or()
                        .like(Resource::getSearchKeywords, trimmedKeyword))
                .orderByDesc(Resource::getUpdatedAt, Resource::getId)
                .last("LIMIT 10");
        List<Resource> resources = resourceMapper.selectList(wrapper);
        logSearch(trimmedKeyword, detectSearchType(trimmedKeyword), resources.isEmpty() ? null : resources.get(0).getId());
        if (resources.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> categoryNameMap = fetchCategoryNameMap(resources);
        Map<Long, ResourceStorage> storageMap = fetchStorageMap(resources);
        Map<Long, List<TagVO>> tagMap = fetchTagMap(resources);

        return resources.stream().map(resource -> {
            FulfillmentQuickSearchItemVO vo = new FulfillmentQuickSearchItemVO();
            vo.setResourceId(resource.getId());
            vo.setResourceCode(resource.getResourceCode());
            vo.setTitle(resource.getTitle());
            vo.setCategoryName(categoryNameMap.getOrDefault(resource.getCategoryId(), ""));
            vo.setTags(tagMap.getOrDefault(resource.getId(), Collections.emptyList()));
            vo.setAuthorizationStatusSnapshot(resource.getAuthorizationStatusSnapshot());
            vo.setStatus(resource.getStatus());
            vo.setUpdatedAt(resource.getUpdatedAt());

            ResourceStorage storage = storageMap.get(resource.getId());
            if (storage != null) {
                vo.setStoragePlatform(storage.getStoragePlatform());
                vo.setShareUrl(storage.getShareUrl());
                vo.setShareCode(storage.getShareCode());
                vo.setExtractCode(storage.getExtractCode());
            }
            return vo;
        }).toList();
    }

    @Override
    public FulfillmentResourceVO getFulfillmentResource(String resourceCode) {
        Resource resource = getResourceByCode(resourceCode);
        ResourceStorage storage = getStorageByResourceId(resource.getId());
        FulfillmentResourceVO vo = new FulfillmentResourceVO();
        vo.setResourceId(resource.getId());
        vo.setTitle(resource.getTitle());
        vo.setResourceCode(resource.getResourceCode());
        vo.setScene(resource.getScene());
        vo.setCategoryName(getCategoryName(resource.getCategoryId()));
        vo.setAuthorizationStatusSnapshot(resource.getAuthorizationStatusSnapshot());
        vo.setStatus(resource.getStatus());
        vo.setStoragePlatform(storage.getStoragePlatform());
        vo.setShareUrl(storage.getShareUrl());
        vo.setShareCode(storage.getShareCode());
        vo.setExtractCode(storage.getExtractCode());
        vo.setDeliveryNote(storage.getDeliveryNote());
        vo.setDeliveryTemplate(buildDeliveryTemplate(resourceCode));
        vo.setRecentDeliveryRecords(listRecentDeliveryRecords(resource.getId(), 5));
        return vo;
    }

    @Override
    public String buildDeliveryTemplate(String resourceCode) {
        Resource resource = getResourceByCode(resourceCode);
        ResourceStorage storage = getStorageByResourceId(resource.getId());
        return FulfillmentTemplateBuilder.build(
                resource.getTitle(),
                resource.getResourceCode(),
                storage.getShareUrl(),
                storage.getShareCode(),
                storage.getExtractCode(),
                StringUtils.hasText(storage.getDeliveryNote()) ? storage.getDeliveryNote() : "如链接失效请及时联系补发"
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markDelivered(FulfillmentMarkDeliveredRequest request) {
        Resource resource = getResourceByCode(request.getResourceCode());
        String deliveryTemplate = buildDeliveryTemplate(resource.getResourceCode());

        DeliveryRecord record = new DeliveryRecord();
        record.setResourceId(resource.getId());
        record.setResourceCode(resource.getResourceCode());
        record.setLeadId(request.getLeadId());
        record.setDeliveryChannel("wechat");
        record.setDeliveryContentSnapshot(deliveryTemplate);
        record.setDeliveryRemark(request.getDeliveryRemark() == null ? "" : request.getDeliveryRemark().trim());
        record.setOperatorName(getCurrentOperatorName());
        deliveryRecordMapper.insert(record);

        if (request.getLeadId() != null) {
            Lead lead = leadMapper.selectById(request.getLeadId());
            if (lead != null) {
                lead.setWechatAddedStatus(1);
                if (lead.getStatus() != null && lead.getStatus() == 0) {
                    lead.setStatus(1);
                }
                lead.setLastFollowUpTime(LocalDateTime.now());
                if (StringUtils.hasText(request.getDeliveryRemark())) {
                    lead.setFollowUpNote(request.getDeliveryRemark().trim());
                }
                leadMapper.updateById(lead);
            }
        }
    }

    @Override
    public PageResult<DeliveryRecordVO> pageDeliveryRecords(String resourceCode, Long pageNum, Long pageSize) {
        Page<DeliveryRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeliveryRecord> wrapper = new LambdaQueryWrapper<DeliveryRecord>()
                .orderByDesc(DeliveryRecord::getCreatedAt, DeliveryRecord::getId);
        if (StringUtils.hasText(resourceCode)) {
            wrapper.eq(DeliveryRecord::getResourceCode, resourceCode.trim());
        }
        Page<DeliveryRecord> result = deliveryRecordMapper.selectPage(page, wrapper);
        List<DeliveryRecordVO> list = result.getRecords().stream().map(this::toDeliveryRecordVO).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), list);
    }

    @Override
    public List<ResourceSearchLogVO> listRecentSearchLogs(int limit) {
        int actualLimit = limit <= 0 ? 10 : Math.min(limit, 50);
        return resourceSearchLogMapper.selectList(new LambdaQueryWrapper<ResourceSearchLog>()
                        .orderByDesc(ResourceSearchLog::getCreatedAt, ResourceSearchLog::getId)
                        .last("LIMIT " + actualLimit))
                .stream()
                .map(log -> {
                    ResourceSearchLogVO vo = new ResourceSearchLogVO();
                    BeanUtils.copyProperties(log, vo);
                    return vo;
                })
                .toList();
    }

    private Resource getResourceByCode(String resourceCode) {
        Resource resource = resourceMapper.selectOne(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getResourceCode, resourceCode)
                .last("LIMIT 1"));
        if (resource == null) {
            throw new BusinessException("未找到对应资源码");
        }
        return resource;
    }

    private ResourceStorage getStorageByResourceId(Long resourceId) {
        ResourceStorage storage = resourceStorageMapper.selectOne(new LambdaQueryWrapper<ResourceStorage>()
                .eq(ResourceStorage::getResourceId, resourceId)
                .eq(ResourceStorage::getStatus, 1)
                .last("LIMIT 1"));
        if (storage == null) {
            throw new BusinessException("该资源尚未绑定网盘信息");
        }
        return storage;
    }

    private String getCategoryName(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        return category == null ? "" : category.getName();
    }

    private Map<Long, String> fetchCategoryNameMap(List<Resource> resources) {
        List<Long> categoryIds = resources.stream().map(Resource::getCategoryId).distinct().toList();
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().in(Category::getId, categoryIds))
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }

    private Map<Long, ResourceStorage> fetchStorageMap(List<Resource> resources) {
        List<Long> resourceIds = resources.stream().map(Resource::getId).toList();
        if (resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return resourceStorageMapper.selectList(new LambdaQueryWrapper<ResourceStorage>()
                        .in(ResourceStorage::getResourceId, resourceIds)
                        .eq(ResourceStorage::getStatus, 1))
                .stream()
                .collect(Collectors.toMap(ResourceStorage::getResourceId, item -> item, (a, b) -> a));
    }

    private Map<Long, List<TagVO>> fetchTagMap(List<Resource> resources) {
        List<Long> resourceIds = resources.stream().map(Resource::getId).toList();
        if (resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ResourceTag> relations = resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                .in(ResourceTag::getResourceId, resourceIds)
                .orderByAsc(ResourceTag::getId));
        if (relations.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> tagIds = relations.stream().map(ResourceTag::getTagId).distinct().toList();
        Map<Long, TagVO> tagVOMap = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                        .in(Tag::getId, tagIds)
                        .eq(Tag::getStatus, 1))
                .stream()
                .map(this::toTagVO)
                .collect(Collectors.toMap(TagVO::getId, item -> item, (a, b) -> a));

        Map<Long, List<TagVO>> result = new LinkedHashMap<>();
        for (ResourceTag relation : relations) {
            TagVO tagVO = tagVOMap.get(relation.getTagId());
            if (tagVO == null) {
                continue;
            }
            result.computeIfAbsent(relation.getResourceId(), key -> new ArrayList<>()).add(tagVO);
        }
        return result;
    }

    private List<DeliveryRecordVO> listRecentDeliveryRecords(Long resourceId, int limit) {
        return deliveryRecordMapper.selectList(new LambdaQueryWrapper<DeliveryRecord>()
                        .eq(DeliveryRecord::getResourceId, resourceId)
                        .orderByDesc(DeliveryRecord::getCreatedAt, DeliveryRecord::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(this::toDeliveryRecordVO)
                .toList();
    }

    private DeliveryRecordVO toDeliveryRecordVO(DeliveryRecord record) {
        DeliveryRecordVO vo = new DeliveryRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }

    private void logSearch(String keyword, String searchType, Long matchedResourceId) {
        ResourceSearchLog log = new ResourceSearchLog();
        log.setKeyword(keyword);
        log.setSearchType(searchType);
        log.setMatchedResourceId(matchedResourceId);
        log.setOperatorName(getCurrentOperatorName());
        resourceSearchLogMapper.insert(log);
    }

    private String detectSearchType(String keyword) {
        if (keyword.startsWith("RES-")) {
            return "resource_code";
        }
        if (keyword.matches("\\d+")) {
            return "id";
        }
        if (keyword.contains("-")) {
            return "slug";
        }
        return "keyword";
    }

    private String getCurrentOperatorName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AdminLoginUser loginUser) {
            return loginUser.getUsername();
        }
        return "系统管理员";
    }
}
