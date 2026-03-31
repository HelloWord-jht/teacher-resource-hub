package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.enums.ResourceSortType;
import com.teacherresourcehub.dto.ResourceAdminQueryRequest;
import com.teacherresourcehub.dto.ResourceQueryRequest;
import com.teacherresourcehub.dto.ResourceSaveRequest;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourcePreview;
import com.teacherresourcehub.entity.ResourceRecommend;
import com.teacherresourcehub.entity.ResourceTag;
import com.teacherresourcehub.entity.Tag;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourcePreviewMapper;
import com.teacherresourcehub.mapper.ResourceRecommendMapper;
import com.teacherresourcehub.mapper.ResourceTagMapper;
import com.teacherresourcehub.mapper.TagMapper;
import com.teacherresourcehub.service.FaqService;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.service.SiteConfigService;
import com.teacherresourcehub.vo.AdminResourceListItemVO;
import com.teacherresourcehub.vo.FaqVO;
import com.teacherresourcehub.vo.ResourceAdminDetailVO;
import com.teacherresourcehub.vo.ResourceDetailVO;
import com.teacherresourcehub.vo.ResourceListItemVO;
import com.teacherresourcehub.vo.ResourceRelationVO;
import com.teacherresourcehub.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.teacherresourcehub.common.util.JsonUtils.parse;
import static com.teacherresourcehub.common.util.JsonUtils.toJson;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourcePreviewMapper resourcePreviewMapper;
    private final ResourceTagMapper resourceTagMapper;
    private final ResourceRecommendMapper resourceRecommendMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final FaqService faqService;
    private final SiteConfigService siteConfigService;

    public ResourceServiceImpl(ResourceMapper resourceMapper,
                               ResourcePreviewMapper resourcePreviewMapper,
                               ResourceTagMapper resourceTagMapper,
                               ResourceRecommendMapper resourceRecommendMapper,
                               CategoryMapper categoryMapper,
                               TagMapper tagMapper,
                               FaqService faqService,
                               SiteConfigService siteConfigService) {
        this.resourceMapper = resourceMapper;
        this.resourcePreviewMapper = resourcePreviewMapper;
        this.resourceTagMapper = resourceTagMapper;
        this.resourceRecommendMapper = resourceRecommendMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.faqService = faqService;
        this.siteConfigService = siteConfigService;
    }

    @Override
    public PageResult<ResourceListItemVO> pageWebResources(ResourceQueryRequest request) {
        Page<Resource> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<Resource>()
                .eq(Resource::getStatus, 1);

        if (request.getCategoryId() != null) {
            wrapper.eq(Resource::getCategoryId, request.getCategoryId());
        }
        if (StringUtils.hasText(request.getGrade())) {
            wrapper.eq(Resource::getGrade, request.getGrade());
        }
        if (StringUtils.hasText(request.getScene())) {
            wrapper.eq(Resource::getScene, request.getScene());
        }
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Resource::getTitle, request.getKeyword())
                    .or()
                    .like(Resource::getSummary, request.getKeyword()));
        }
        if (request.getTagId() != null) {
            List<Long> resourceIds = resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                            .eq(ResourceTag::getTagId, request.getTagId()))
                    .stream()
                    .map(ResourceTag::getResourceId)
                    .distinct()
                    .toList();
            if (resourceIds.isEmpty()) {
                return PageResult.of(request.getPageNum(), request.getPageSize(), 0L, Collections.emptyList());
            }
            wrapper.in(Resource::getId, resourceIds);
        }

        applySort(wrapper, ResourceSortType.fromValue(request.getSortType()));
        Page<Resource> result = resourceMapper.selectPage(page, wrapper);
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), buildResourceListItems(result.getRecords()));
    }

    @Override
    public ResourceDetailVO getWebResourceDetail(Long id) {
        Resource resource = resourceMapper.selectOne(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getId, id)
                .eq(Resource::getStatus, 1)
                .last("LIMIT 1"));
        if (resource == null) {
            throw new BusinessException("资源不存在或已下线");
        }

        resource.setViewCount(resource.getViewCount() == null ? 1 : resource.getViewCount() + 1);
        resourceMapper.updateById(resource);

        ResourceDetailVO vo = new ResourceDetailVO();
        BeanUtils.copyProperties(resource, vo);
        vo.setCategoryName(getCategoryName(resource.getCategoryId()));
        vo.setPreviewImages(listPreviewImages(resource.getId()));
        vo.setTags(fetchTagMap(List.of(resource.getId())).getOrDefault(resource.getId(), Collections.emptyList()));
        vo.setContentItems(parseContentItems(resource.getContentItemsJson()));
        vo.setFaqList(faqService.listEnabledFaqs().stream().limit(6).toList());
        vo.setWechatConsult(siteConfigService.getWechatConsult());
        vo.setRelatedResources(listPublishedResourceRelationsByIds(listRecommendedIds(resource.getId())));
        return vo;
    }

    @Override
    public List<ResourceListItemVO> listPublishedResourceListItemsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .in(Resource::getId, ids)
                .eq(Resource::getStatus, 1));
        Map<Long, ResourceListItemVO> map = buildResourceListItems(resources).stream()
                .collect(Collectors.toMap(ResourceListItemVO::getId, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).toList();
    }

    @Override
    public List<ResourceRelationVO> listPublishedResourceRelationsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .in(Resource::getId, ids)
                .eq(Resource::getStatus, 1));
        Map<Long, ResourceRelationVO> map = resources.stream()
                .map(this::toResourceRelationVO)
                .collect(Collectors.toMap(ResourceRelationVO::getId, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).toList();
    }

    @Override
    public PageResult<AdminResourceListItemVO> pageAdminResources(ResourceAdminQueryRequest request) {
        Page<Resource> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Resource::getTitle, request.getKeyword())
                    .or()
                    .like(Resource::getSummary, request.getKeyword()));
        }
        if (request.getCategoryId() != null) {
            wrapper.eq(Resource::getCategoryId, request.getCategoryId());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Resource::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(Resource::getSortOrder, Resource::getCreatedAt, Resource::getId);
        Page<Resource> result = resourceMapper.selectPage(page, wrapper);

        Map<Long, String> categoryMap = fetchCategoryNameMap(result.getRecords().stream()
                .map(Resource::getCategoryId)
                .collect(Collectors.toSet()));
        List<AdminResourceListItemVO> list = result.getRecords().stream().map(item -> {
            AdminResourceListItemVO vo = new AdminResourceListItemVO();
            BeanUtils.copyProperties(item, vo);
            vo.setCategoryName(categoryMap.getOrDefault(item.getCategoryId(), ""));
            return vo;
        }).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), list);
    }

    @Override
    public ResourceAdminDetailVO getAdminResourceDetail(Long id) {
        Resource resource = getResourceOrThrow(id);
        ResourceAdminDetailVO vo = new ResourceAdminDetailVO();
        BeanUtils.copyProperties(resource, vo);
        vo.setContentItems(parseContentItems(resource.getContentItemsJson()));
        vo.setPreviewImageList(listPreviewImages(id));
        vo.setTagIdList(listTagIds(id));
        vo.setRecommendedResourceIdList(listRecommendedIds(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createResource(ResourceSaveRequest request) {
        validateResourceRequest(null, request);
        Resource resource = new Resource();
        fillResourceEntity(resource, request, null);
        resource.setViewCount(0);
        resourceMapper.insert(resource);
        saveRelations(resource.getId(), request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateResource(Long id, ResourceSaveRequest request) {
        Resource resource = getResourceOrThrow(id);
        validateResourceRequest(id, request);
        fillResourceEntity(resource, request, resource);
        resourceMapper.updateById(resource);
        clearRelations(id);
        saveRelations(id, request);
    }

    @Override
    public void updateResourceStatus(Long id, Integer status) {
        Resource resource = getResourceOrThrow(id);
        resource.setStatus(status);
        if (status != null && status == 1 && resource.getPublishTime() == null) {
            resource.setPublishTime(LocalDateTime.now());
        }
        resourceMapper.updateById(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(Long id) {
        getResourceOrThrow(id);
        resourceMapper.deleteById(id);
        clearRelations(id);
    }

    @Override
    public long countTotal() {
        Long count = resourceMapper.selectCount(new LambdaQueryWrapper<Resource>());
        return count == null ? 0L : count;
    }

    private void validateResourceRequest(Long id, ResourceSaveRequest request) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BusinessException("所选分类不存在");
        }

        List<String> contentItems = sanitizeStringList(request.getContentItems());
        if (contentItems.isEmpty()) {
            throw new BusinessException("包含内容清单不能为空");
        }

        List<String> previewImages = sanitizeStringList(request.getPreviewImageList());
        if (previewImages.isEmpty()) {
            throw new BusinessException("至少需要一张预览图");
        }

        Resource exists = resourceMapper.selectOne(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getSlug, request.getSlug())
                .last("LIMIT 1"));
        if (exists != null && !exists.getId().equals(id)) {
            throw new BusinessException("资源别名已存在");
        }

        List<Long> tagIds = sanitizeLongList(request.getTagIdList(), null);
        if (tagIds.isEmpty()) {
            throw new BusinessException("至少选择一个标签");
        }
        Long tagCount = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, tagIds));
        if (tagCount == null || tagCount.intValue() != tagIds.size()) {
            throw new BusinessException("标签数据不完整，请重新选择");
        }

        List<Long> recommendedIds = sanitizeLongList(request.getRecommendedResourceIdList(), id);
        if (!recommendedIds.isEmpty()) {
            Long resourceCount = resourceMapper.selectCount(new LambdaQueryWrapper<Resource>()
                    .in(Resource::getId, recommendedIds));
            if (resourceCount == null || resourceCount.intValue() != recommendedIds.size()) {
                throw new BusinessException("推荐资源数据不完整，请重新选择");
            }
        }
    }

    private void fillResourceEntity(Resource target, ResourceSaveRequest request, Resource oldResource) {
        target.setTitle(request.getTitle());
        target.setSlug(request.getSlug());
        target.setCategoryId(request.getCategoryId());
        target.setSummary(request.getSummary());
        target.setCoverImage(request.getCoverImage());
        target.setDisplayPrice(request.getDisplayPrice());
        target.setGrade(request.getGrade());
        target.setScene(request.getScene());
        target.setContentItemsJson(toJson(sanitizeStringList(request.getContentItems())));
        target.setDescriptionHtml(request.getDescriptionHtml());
        target.setUsageNotice(request.getUsageNotice());
        target.setStatus(request.getStatus());
        target.setIsRecommended(request.getIsRecommended());
        target.setSortOrder(request.getSortOrder());

        if (request.getPublishTime() != null) {
            target.setPublishTime(request.getPublishTime());
        } else if (oldResource != null && oldResource.getPublishTime() != null) {
            target.setPublishTime(oldResource.getPublishTime());
        } else if (request.getStatus() != null && request.getStatus() == 1) {
            target.setPublishTime(LocalDateTime.now());
        } else {
            target.setPublishTime(null);
        }
    }

    private void saveRelations(Long resourceId, ResourceSaveRequest request) {
        List<String> previewImages = sanitizeStringList(request.getPreviewImageList());
        for (int i = 0; i < previewImages.size(); i++) {
            ResourcePreview preview = new ResourcePreview();
            preview.setResourceId(resourceId);
            preview.setImageUrl(previewImages.get(i));
            preview.setSortOrder((previewImages.size() - i) * 10);
            resourcePreviewMapper.insert(preview);
        }

        List<Long> tagIds = sanitizeLongList(request.getTagIdList(), null);
        for (Long tagId : tagIds) {
            ResourceTag resourceTag = new ResourceTag();
            resourceTag.setResourceId(resourceId);
            resourceTag.setTagId(tagId);
            resourceTagMapper.insert(resourceTag);
        }

        List<Long> recommendedIds = sanitizeLongList(request.getRecommendedResourceIdList(), resourceId);
        for (int i = 0; i < recommendedIds.size(); i++) {
            ResourceRecommend recommend = new ResourceRecommend();
            recommend.setResourceId(resourceId);
            recommend.setRecommendedResourceId(recommendedIds.get(i));
            recommend.setSortOrder((recommendedIds.size() - i) * 10);
            resourceRecommendMapper.insert(recommend);
        }
    }

    private void clearRelations(Long resourceId) {
        resourcePreviewMapper.delete(new LambdaQueryWrapper<ResourcePreview>()
                .eq(ResourcePreview::getResourceId, resourceId));
        resourceTagMapper.delete(new LambdaQueryWrapper<ResourceTag>()
                .eq(ResourceTag::getResourceId, resourceId));
        resourceRecommendMapper.delete(new LambdaQueryWrapper<ResourceRecommend>()
                .eq(ResourceRecommend::getResourceId, resourceId));
    }

    private Resource getResourceOrThrow(Long id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        return resource;
    }

    private List<ResourceListItemVO> buildResourceListItems(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> categoryIds = resources.stream().map(Resource::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryMap = fetchCategoryNameMap(categoryIds);
        Map<Long, List<TagVO>> tagMap = fetchTagMap(resources.stream().map(Resource::getId).toList());

        return resources.stream().map(item -> {
            ResourceListItemVO vo = new ResourceListItemVO();
            BeanUtils.copyProperties(item, vo);
            vo.setCategoryName(categoryMap.getOrDefault(item.getCategoryId(), ""));
            vo.setTags(tagMap.getOrDefault(item.getId(), Collections.emptyList()));
            return vo;
        }).toList();
    }

    private Map<Long, String> fetchCategoryNameMap(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .in(Category::getId, categoryIds))
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }

    private Map<Long, List<TagVO>> fetchTagMap(List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ResourceTag> relations = resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                .in(ResourceTag::getResourceId, resourceIds)
                .orderByAsc(ResourceTag::getId));
        if (relations.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> tagIds = relations.stream().map(ResourceTag::getTagId).distinct().toList();
        Map<Long, TagVO> tagMap = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                        .in(Tag::getId, tagIds)
                        .eq(Tag::getStatus, 1))
                .stream()
                .map(this::toTagVO)
                .collect(Collectors.toMap(TagVO::getId, Function.identity(), (a, b) -> a));

        Map<Long, List<TagVO>> result = new LinkedHashMap<>();
        for (ResourceTag relation : relations) {
            TagVO tagVO = tagMap.get(relation.getTagId());
            if (tagVO == null) {
                continue;
            }
            result.computeIfAbsent(relation.getResourceId(), key -> new ArrayList<>()).add(tagVO);
        }
        return result;
    }

    private void applySort(LambdaQueryWrapper<Resource> wrapper, ResourceSortType sortType) {
        switch (sortType) {
            case HOT -> wrapper.orderByDesc(Resource::getViewCount, Resource::getSortOrder, Resource::getPublishTime, Resource::getId);
            case RECOMMENDED -> wrapper.orderByDesc(Resource::getIsRecommended, Resource::getSortOrder, Resource::getPublishTime, Resource::getId);
            case LATEST -> wrapper.orderByDesc(Resource::getPublishTime, Resource::getSortOrder, Resource::getId);
        }
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) {
            return "";
        }
        Category category = categoryMapper.selectById(categoryId);
        return category == null ? "" : category.getName();
    }

    private List<String> listPreviewImages(Long resourceId) {
        return resourcePreviewMapper.selectList(new LambdaQueryWrapper<ResourcePreview>()
                        .eq(ResourcePreview::getResourceId, resourceId)
                        .orderByDesc(ResourcePreview::getSortOrder, ResourcePreview::getId))
                .stream()
                .map(ResourcePreview::getImageUrl)
                .toList();
    }

    private List<Long> listTagIds(Long resourceId) {
        return resourceTagMapper.selectList(new LambdaQueryWrapper<ResourceTag>()
                        .eq(ResourceTag::getResourceId, resourceId)
                        .orderByAsc(ResourceTag::getId))
                .stream()
                .map(ResourceTag::getTagId)
                .toList();
    }

    private List<Long> listRecommendedIds(Long resourceId) {
        return resourceRecommendMapper.selectList(new LambdaQueryWrapper<ResourceRecommend>()
                        .eq(ResourceRecommend::getResourceId, resourceId)
                        .orderByDesc(ResourceRecommend::getSortOrder, ResourceRecommend::getId))
                .stream()
                .map(ResourceRecommend::getRecommendedResourceId)
                .toList();
    }

    private List<String> parseContentItems(String contentItemsJson) {
        if (!StringUtils.hasText(contentItemsJson)) {
            return Collections.emptyList();
        }
        return parse(contentItemsJson, new TypeReference<List<String>>() {
        });
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }

    private ResourceRelationVO toResourceRelationVO(Resource resource) {
        ResourceRelationVO vo = new ResourceRelationVO();
        vo.setId(resource.getId());
        vo.setTitle(resource.getTitle());
        vo.setCoverImage(resource.getCoverImage());
        vo.setDisplayPrice(resource.getDisplayPrice());
        vo.setGrade(resource.getGrade());
        vo.setScene(resource.getScene());
        return vo;
    }

    private List<String> sanitizeStringList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> set = new LinkedHashSet<>();
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                set.add(value.trim());
            }
        }
        return new ArrayList<>(set);
    }

    private List<Long> sanitizeLongList(List<Long> values, Long excludeId) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> set = new LinkedHashSet<>();
        for (Long value : values) {
            if (value != null && !Objects.equals(value, excludeId)) {
                set.add(value);
            }
        }
        return new ArrayList<>(set);
    }
}
