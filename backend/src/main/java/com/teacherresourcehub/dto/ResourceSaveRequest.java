package com.teacherresourcehub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ResourceSaveRequest {

    @Size(max = 50, message = "资源码长度不能超过50个字符")
    private String resourceCode;

    @NotBlank(message = "资源标题不能为空")
    @Size(max = 150, message = "资源标题长度不能超过150个字符")
    private String title;

    @NotBlank(message = "资源别名不能为空")
    @Size(max = 180, message = "资源别名长度不能超过180个字符")
    private String slug;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotNull(message = "来源不能为空")
    private Long sourceId;

    @NotBlank(message = "资源简介不能为空")
    @Size(max = 500, message = "资源简介长度不能超过500个字符")
    private String summary;

    @NotBlank(message = "封面图URL不能为空")
    @Size(max = 255, message = "封面图URL长度不能超过255个字符")
    private String coverImage;

    @NotNull(message = "展示价格不能为空")
    @DecimalMin(value = "0.00", message = "展示价格不能小于0")
    private BigDecimal displayPrice;

    @NotBlank(message = "适用年级不能为空")
    @Size(max = 50, message = "适用年级长度不能超过50个字符")
    private String grade;

    @NotBlank(message = "适用场景不能为空")
    @Size(max = 50, message = "适用场景长度不能超过50个字符")
    private String scene;

    @NotEmpty(message = "包含内容清单不能为空")
    private List<String> contentItems;

    @NotBlank(message = "资源说明不能为空")
    private String descriptionHtml;

    @NotBlank(message = "使用说明不能为空")
    @Size(max = 1000, message = "使用说明长度不能超过1000个字符")
    private String usageNotice;

    @NotBlank(message = "交付说明不能为空")
    @Size(max = 1000, message = "交付说明长度不能超过1000个字符")
    private String deliveryNotice;

    @NotNull(message = "资源状态不能为空")
    private Integer status;

    @NotNull(message = "推荐状态不能为空")
    private Integer isRecommended;

    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;

    private LocalDateTime publishTime;

    @NotEmpty(message = "预览图不能为空")
    private List<String> previewImageList;

    @NotEmpty(message = "标签不能为空")
    private List<Long> tagIdList;

    private List<Long> recommendedResourceIdList;

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(BigDecimal displayPrice) {
        this.displayPrice = displayPrice;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public List<String> getContentItems() {
        return contentItems;
    }

    public void setContentItems(List<String> contentItems) {
        this.contentItems = contentItems;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getUsageNotice() {
        return usageNotice;
    }

    public void setUsageNotice(String usageNotice) {
        this.usageNotice = usageNotice;
    }

    public String getDeliveryNotice() {
        return deliveryNotice;
    }

    public void setDeliveryNotice(String deliveryNotice) {
        this.deliveryNotice = deliveryNotice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(Integer isRecommended) {
        this.isRecommended = isRecommended;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public List<String> getPreviewImageList() {
        return previewImageList;
    }

    public void setPreviewImageList(List<String> previewImageList) {
        this.previewImageList = previewImageList;
    }

    public List<Long> getTagIdList() {
        return tagIdList;
    }

    public void setTagIdList(List<Long> tagIdList) {
        this.tagIdList = tagIdList;
    }

    public List<Long> getRecommendedResourceIdList() {
        return recommendedResourceIdList;
    }

    public void setRecommendedResourceIdList(List<Long> recommendedResourceIdList) {
        this.recommendedResourceIdList = recommendedResourceIdList;
    }
}
