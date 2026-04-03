package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.enums.AuthorizationStatus;
import com.teacherresourcehub.common.enums.ImportTaskStatus;
import com.teacherresourcehub.common.util.JsonUtils;
import com.teacherresourcehub.common.util.ResourceCodeGenerator;
import com.teacherresourcehub.common.util.ResourceImportDecisionHelper;
import com.teacherresourcehub.common.util.SearchKeywordTokenizer;
import com.teacherresourcehub.dto.ImportTaskSaveRequest;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.ImportTask;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourcePreview;
import com.teacherresourcehub.entity.ResourceSource;
import com.teacherresourcehub.entity.ResourceStorage;
import com.teacherresourcehub.entity.ResourceTag;
import com.teacherresourcehub.entity.Tag;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourcePreviewMapper;
import com.teacherresourcehub.mapper.ResourceSourceMapper;
import com.teacherresourcehub.mapper.ResourceStorageMapper;
import com.teacherresourcehub.mapper.ResourceTagMapper;
import com.teacherresourcehub.mapper.TagMapper;
import com.teacherresourcehub.service.ImportTaskService;
import com.teacherresourcehub.service.ResourceFileService;
import com.teacherresourcehub.vo.ImportTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImportTaskServiceImpl implements ImportTaskService {

    private final ImportTaskMapper importTaskMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ResourceMapper resourceMapper;
    private final ResourcePreviewMapper resourcePreviewMapper;
    private final ResourceTagMapper resourceTagMapper;
    private final ResourceStorageMapper resourceStorageMapper;
    private final ResourceSourceMapper resourceSourceMapper;
    private final ResourceFileService resourceFileService;
    private final TransactionTemplate transactionTemplate;

    public ImportTaskServiceImpl(ImportTaskMapper importTaskMapper,
                                 CategoryMapper categoryMapper,
                                 TagMapper tagMapper,
                                 ResourceMapper resourceMapper,
                                 ResourcePreviewMapper resourcePreviewMapper,
                                 ResourceTagMapper resourceTagMapper,
                                 ResourceStorageMapper resourceStorageMapper,
                                 ResourceSourceMapper resourceSourceMapper,
                                 ResourceFileService resourceFileService,
                                 TransactionTemplate transactionTemplate) {
        this.importTaskMapper = importTaskMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.resourceMapper = resourceMapper;
        this.resourcePreviewMapper = resourcePreviewMapper;
        this.resourceTagMapper = resourceTagMapper;
        this.resourceStorageMapper = resourceStorageMapper;
        this.resourceSourceMapper = resourceSourceMapper;
        this.resourceFileService = resourceFileService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public PageResult<ImportTaskVO> page(String importStatus, Long pageNum, Long pageSize) {
        Page<ImportTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ImportTask> wrapper = new LambdaQueryWrapper<ImportTask>()
                .orderByDesc(ImportTask::getCreatedAt, ImportTask::getId);
        if (StringUtils.hasText(importStatus)) {
            wrapper.eq(ImportTask::getImportStatus, importStatus.trim());
        }
        Page<ImportTask> result = importTaskMapper.selectPage(page, wrapper);
        List<ImportTaskVO> list = result.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), list);
    }

    @Override
    public ImportTaskVO getById(Long id) {
        return toVO(getOrThrow(id));
    }

    @Override
    public void create(ImportTaskSaveRequest request) {
        ImportTask task = new ImportTask();
        task.setTaskName(request.getTaskName().trim());
        task.setImportType("json_payload");
        task.setRawPayload(request.getRawPayload());
        task.setImportStatus(ImportTaskStatus.PENDING.getCode());
        task.setGeneratedResourceCode("");
        task.setTotalFileCount(0);
        task.setRecognizedFileCount(0);
        task.setProcessedFileCount(0);
        task.setPreviewSuccessCount(0);
        task.setPreviewFailedCount(0);
        task.setUnsupportedFileCount(0);
        task.setUnzipStatus("not_applicable");
        task.setPreviewStatusSummary("{\"pending\":0,\"processing\":0,\"success\":0,\"failed\":0,\"unsupported\":0}");
        task.setRecommendedTagIdsJson("[]");
        task.setExecutionResult("等待执行。");
        task.setOperatorName("系统管理员");
        importTaskMapper.insert(task);
    }

    @Override
    public void execute(Long id) {
        ImportTask task = getOrThrow(id);
        task.setImportStatus(ImportTaskStatus.PROCESSING.getCode());
        task.setExecutionResult("正在执行导入与预览绑定，请稍候。");
        importTaskMapper.updateById(task);
        try {
            transactionTemplate.executeWithoutResult(status -> executeInternal(task));
        } catch (RuntimeException exception) {
            task.setImportStatus(ImportTaskStatus.FAILED.getCode());
            task.setExecutionResult("导入失败：" + exception.getMessage());
            task.setExecutedAt(LocalDateTime.now());
            importTaskMapper.updateById(task);
            throw exception;
        }
    }

    @Override
    public List<ImportTaskVO> listRecent(int limit) {
        return importTaskMapper.selectList(new LambdaQueryWrapper<ImportTask>()
                        .orderByDesc(ImportTask::getCreatedAt, ImportTask::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(this::toVO)
                .toList();
    }

    private Resource buildResource(ImportPayload payload, ResourceSource source, Category category) {
        Resource resource = new Resource();
        String resourceCode = nextResourceCode(category.getCode());
        resource.setResourceCode(resourceCode);
        resource.setTitle(payload.getTitle().trim());
        resource.setSlug(resourceCode.toLowerCase().replace("RES-", "res-"));
        resource.setCategoryId(category.getId());
        resource.setSourceId(source.getId());
        resource.setSummary(StringUtils.hasText(payload.getSummary()) ? payload.getSummary().trim() : summarize(payload.getTitle()));
        resource.setCoverImage(firstOrDefault(payload.getPreviewImages(), "https://placehold.co/1200x760/png?text=" + resourceCode));
        resource.setDisplayPrice(payload.getPrice() == null ? BigDecimal.ZERO : payload.getPrice());
        resource.setGrade(valueOrEmpty(payload.getGrade()));
        resource.setScene(valueOrEmpty(payload.getScene()));
        List<String> contentItems = sanitizeStrings(payload.getContentItems());
        resource.setContentItemsJson(JsonUtils.toJson(contentItems));
        resource.setDescriptionHtml("<p>" + summarize(payload.getSummary()) + "</p>");
        resource.setUsageNotice(valueOrEmpty(payload.getUsageNotice()));
        resource.setDeliveryNotice("本站不做在线支付，确认需求后通过微信沟通交付；加微信时发送资源码，可更快匹配资料。");
        resource.setSearchKeywords(SearchKeywordTokenizer.joinAsSearchKeywords(
                payload.getTitle(),
                payload.getSummary(),
                payload.getScene(),
                String.join(" ", contentItems)
        ));
        resource.setAuthorizationStatusSnapshot(source.getAuthorizationStatus());
        resource.setPreviewCount(sanitizeStrings(payload.getPreviewImages()).size());
        resource.setContentItemCount(contentItems.size());
        resource.setStatus(AuthorizationStatus.APPROVED.name().equals(source.getAuthorizationStatus()) ? 1 : 0);
        resource.setIsRecommended(0);
        resource.setViewCount(0);
        resource.setConsultCount(0);
        resource.setSortOrder(0);
        resource.setPublishTime(resource.getStatus() == 1 ? LocalDateTime.now() : null);
        return resource;
    }

    private void savePreviews(Long resourceId, List<String> previewImages) {
        List<String> images = sanitizeStrings(previewImages);
        for (int index = 0; index < images.size(); index++) {
            ResourcePreview preview = new ResourcePreview();
            preview.setResourceId(resourceId);
            preview.setImageUrl(images.get(index));
            preview.setSortOrder((images.size() - index) * 10);
            resourcePreviewMapper.insert(preview);
        }
    }

    private void saveTags(Long resourceId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ResourceTag relation = new ResourceTag();
            relation.setResourceId(resourceId);
            relation.setTagId(tagId);
            resourceTagMapper.insert(relation);
        }
    }

    private void saveStorage(Long resourceId, ImportPayload payload) {
        ResourceStorage storage = new ResourceStorage();
        storage.setResourceId(resourceId);
        storage.setStoragePlatform(valueOrEmpty(payload.getStoragePlatform()));
        storage.setShareUrl(valueOrEmpty(payload.getShareUrl()));
        storage.setShareCode(valueOrEmpty(payload.getShareCode()));
        storage.setExtractCode(valueOrEmpty(payload.getExtractCode()));
        storage.setDeliveryNote("如链接失效请及时联系补发");
        storage.setStatus(1);
        resourceStorageMapper.insert(storage);
    }

    private List<Long> recommendTagIds(ImportPayload payload) {
        List<Tag> allTags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getStatus, 1));
        if (allTags.isEmpty()) {
            return Collections.emptyList();
        }
        String tokenSource = SearchKeywordTokenizer.joinAsSearchKeywords(
                payload.getTitle(),
                payload.getSummary(),
                payload.getScene(),
                String.join(" ", sanitizeStrings(payload.getContentItems()))
        );
        Set<Long> result = new LinkedHashSet<>();
        for (Tag tag : allTags) {
            String tagName = tag.getName();
            if ((payload.getTags() != null && payload.getTags().stream().anyMatch(tagName::equalsIgnoreCase))
                    || tokenSource.contains(tagName)) {
                result.add(tag.getId());
            }
        }
        return new ArrayList<>(result);
    }

    private String buildExecutionResult(ResourceSource source, Category category) {
        if (AuthorizationStatus.APPROVED.name().equals(source.getAuthorizationStatus())) {
            return "导入成功，自动命中%s分类；来源已审核通过，资源已生成。".formatted(category.getName());
        }
        return "导入成功，自动命中%s分类；由于来源未审核通过，资源默认下线。".formatted(category.getName());
    }

    private String nextResourceCode(String categoryCode) {
        LocalDate today = LocalDate.now();
        String prefix = "RES-" + categoryCode + "-" + today.format(DateTimeFormatter.BASIC_ISO_DATE) + "-";
        List<Resource> sameDayResources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .likeRight(Resource::getResourceCode, prefix));
        int maxSequence = 0;
        for (Resource resource : sameDayResources) {
            String code = resource.getResourceCode();
            if (!StringUtils.hasText(code) || code.length() < 4) {
                continue;
            }
            try {
                maxSequence = Math.max(maxSequence, Integer.parseInt(code.substring(code.length() - 4)));
            } catch (NumberFormatException ignored) {
            }
        }
        return ResourceCodeGenerator.generate(categoryCode, today, maxSequence + 1);
    }

    private ImportTask getOrThrow(Long id) {
        ImportTask task = importTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("导入任务不存在");
        }
        return task;
    }

    private ImportTaskVO toVO(ImportTask task) {
        ImportTaskVO vo = new ImportTaskVO();
        BeanUtils.copyProperties(task, vo);
        if (StringUtils.hasText(task.getRecommendedTagIdsJson())) {
            vo.setRecommendedTagIds(JsonUtils.parse(task.getRecommendedTagIdsJson(), new TypeReference<List<Long>>() {}));
        } else {
            vo.setRecommendedTagIds(Collections.emptyList());
        }
        return vo;
    }

    private String summarize(String text) {
        if (!StringUtils.hasText(text)) {
            return "导入任务生成的资源，请补充更完整的资源说明。";
        }
        return text.trim().length() > 120 ? text.trim().substring(0, 120) : text.trim();
    }

    private String firstOrDefault(List<String> values, String defaultValue) {
        List<String> sanitized = sanitizeStrings(values);
        return sanitized.isEmpty() ? defaultValue : sanitized.get(0);
    }

    private List<String> sanitizeStrings(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        return values.stream().filter(StringUtils::hasText).map(String::trim).distinct().toList();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void executeInternal(ImportTask task) {
        ImportPayload payload = JsonUtils.parse(task.getRawPayload(), ImportPayload.class);
        if (!StringUtils.hasText(payload.getTitle())) {
            throw new BusinessException("导入JSON缺少标题");
        }
        if (payload.getSourceId() == null) {
            throw new BusinessException("导入JSON缺少来源ID");
        }
        ResourceSource source = resourceSourceMapper.selectById(payload.getSourceId());
        if (source == null) {
            throw new BusinessException("导入来源不存在");
        }

        String recommendedCategoryCode = ResourceImportDecisionHelper.recommendCategoryCode(payload.getTitle(), payload.getScene());
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getCode, recommendedCategoryCode)
                .last("LIMIT 1"));
        if (category == null) {
            throw new BusinessException("未找到推荐分类");
        }

        List<Long> recommendedTagIds = recommendTagIds(payload);
        Resource resource = buildResource(payload, source, category);
        resourceMapper.insert(resource);
        savePreviews(resource.getId(), payload.getPreviewImages());
        saveTags(resource.getId(), recommendedTagIds);
        saveStorage(resource.getId(), payload);
        resourceFileService.bindImportTaskFilesToResource(task.getId(), resource.getId());

        task.setImportStatus(resolveTaskStatusAfterExecution(task));
        task.setGeneratedResourceId(resource.getId());
        task.setGeneratedResourceCode(resource.getResourceCode());
        task.setRecommendedCategoryId(category.getId());
        task.setRecommendedTagIdsJson(JsonUtils.toJson(recommendedTagIds));
        task.setExecutionResult(buildExecutionResult(source, category));
        task.setExecutedAt(LocalDateTime.now());
        importTaskMapper.updateById(task);
    }

    private String resolveTaskStatusAfterExecution(ImportTask task) {
        int successCount = task.getPreviewSuccessCount() == null ? 0 : task.getPreviewSuccessCount();
        int failedCount = task.getPreviewFailedCount() == null ? 0 : task.getPreviewFailedCount();
        int unsupportedCount = task.getUnsupportedFileCount() == null ? 0 : task.getUnsupportedFileCount();
        int totalCount = task.getTotalFileCount() == null ? 0 : task.getTotalFileCount();
        if (totalCount == 0) {
            return ImportTaskStatus.SUCCESS.getCode();
        }
        if (successCount > 0 && (failedCount > 0 || unsupportedCount > 0 || successCount < totalCount)) {
            return ImportTaskStatus.PARTIAL_SUCCESS.getCode();
        }
        if (successCount > 0) {
            return ImportTaskStatus.SUCCESS.getCode();
        }
        return ImportTaskStatus.FAILED.getCode();
    }

    public static class ImportPayload {
        private String title;
        private String summary;
        private String grade;
        private String scene;
        private List<String> contentItems;
        private List<String> previewImages;
        private String storagePlatform;
        private String shareUrl;
        private String shareCode;
        private String extractCode;
        private Long sourceId;
        private List<String> tags;
        private BigDecimal price;
        private String usageNotice;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public String getScene() { return scene; }
        public void setScene(String scene) { this.scene = scene; }
        public List<String> getContentItems() { return contentItems; }
        public void setContentItems(List<String> contentItems) { this.contentItems = contentItems; }
        public List<String> getPreviewImages() { return previewImages; }
        public void setPreviewImages(List<String> previewImages) { this.previewImages = previewImages; }
        public String getStoragePlatform() { return storagePlatform; }
        public void setStoragePlatform(String storagePlatform) { this.storagePlatform = storagePlatform; }
        public String getShareUrl() { return shareUrl; }
        public void setShareUrl(String shareUrl) { this.shareUrl = shareUrl; }
        public String getShareCode() { return shareCode; }
        public void setShareCode(String shareCode) { this.shareCode = shareCode; }
        public String getExtractCode() { return extractCode; }
        public void setExtractCode(String extractCode) { this.extractCode = extractCode; }
        public Long getSourceId() { return sourceId; }
        public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getUsageNotice() { return usageNotice; }
        public void setUsageNotice(String usageNotice) { this.usageNotice = usageNotice; }
    }
}
