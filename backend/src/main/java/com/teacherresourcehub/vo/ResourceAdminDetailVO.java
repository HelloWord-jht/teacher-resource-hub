package com.teacherresourcehub.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ResourceAdminDetailVO {

    private Long id;
    private String resourceCode;
    private String title;
    private String slug;
    private Long categoryId;
    private Long sourceId;
    private String summary;
    private String coverImage;
    private BigDecimal displayPrice;
    private String grade;
    private String scene;
    private List<String> contentItems;
    private String descriptionHtml;
    private String usageNotice;
    private String deliveryNotice;
    private String searchKeywords;
    private String authorizationStatusSnapshot;
    private Integer status;
    private Integer isRecommended;
    private Integer viewCount;
    private Integer consultCount;
    private Integer previewCount;
    private Integer contentItemCount;
    private Integer sortOrder;
    private LocalDateTime publishTime;
    private List<String> previewImageList;
    private List<Long> tagIdList;
    private List<Long> recommendedResourceIdList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getAuthorizationStatusSnapshot() {
        return authorizationStatusSnapshot;
    }

    public void setAuthorizationStatusSnapshot(String authorizationStatusSnapshot) {
        this.authorizationStatusSnapshot = authorizationStatusSnapshot;
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

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getConsultCount() {
        return consultCount;
    }

    public void setConsultCount(Integer consultCount) {
        this.consultCount = consultCount;
    }

    public Integer getPreviewCount() {
        return previewCount;
    }

    public void setPreviewCount(Integer previewCount) {
        this.previewCount = previewCount;
    }

    public Integer getContentItemCount() {
        return contentItemCount;
    }

    public void setContentItemCount(Integer contentItemCount) {
        this.contentItemCount = contentItemCount;
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
