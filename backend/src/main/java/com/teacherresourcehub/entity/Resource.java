package com.teacherresourcehub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("resource")
public class Resource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String resourceCode;

    private String title;

    private String slug;

    private Long categoryId;

    private Long sourceId;

    private Long primaryFileId;

    private String summary;

    private String coverImage;

    private BigDecimal displayPrice;

    private String grade;

    private String scene;

    @TableField("content_items_json")
    private String contentItemsJson;

    private String descriptionHtml;

    private String usageNotice;

    private String deliveryNotice;

    private String searchKeywords;

    private String authorizationStatusSnapshot;

    private Integer previewCount;

    private String previewAvailableStatus;

    private String previewMode;

    private Integer contentItemCount;

    private Integer status;

    private Integer isRecommended;

    private Integer viewCount;

    private Integer consultCount;

    private Integer sortOrder;

    private LocalDateTime publishTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer isDeleted;

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

    public Long getPrimaryFileId() {
        return primaryFileId;
    }

    public void setPrimaryFileId(Long primaryFileId) {
        this.primaryFileId = primaryFileId;
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

    public String getContentItemsJson() {
        return contentItemsJson;
    }

    public void setContentItemsJson(String contentItemsJson) {
        this.contentItemsJson = contentItemsJson;
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

    public Integer getPreviewCount() {
        return previewCount;
    }

    public void setPreviewCount(Integer previewCount) {
        this.previewCount = previewCount;
    }

    public String getPreviewAvailableStatus() {
        return previewAvailableStatus;
    }

    public void setPreviewAvailableStatus(String previewAvailableStatus) {
        this.previewAvailableStatus = previewAvailableStatus;
    }

    public String getPreviewMode() {
        return previewMode;
    }

    public void setPreviewMode(String previewMode) {
        this.previewMode = previewMode;
    }

    public Integer getContentItemCount() {
        return contentItemCount;
    }

    public void setContentItemCount(Integer contentItemCount) {
        this.contentItemCount = contentItemCount;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
