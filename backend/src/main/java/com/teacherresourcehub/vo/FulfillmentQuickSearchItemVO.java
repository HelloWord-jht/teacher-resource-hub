package com.teacherresourcehub.vo;

import java.time.LocalDateTime;
import java.util.List;

public class FulfillmentQuickSearchItemVO {

    private Long resourceId;
    private String resourceCode;
    private String title;
    private String categoryName;
    private List<TagVO> tags;
    private String authorizationStatusSnapshot;
    private Integer status;
    private String storagePlatform;
    private String shareUrl;
    private String shareCode;
    private String extractCode;
    private LocalDateTime updatedAt;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<TagVO> getTags() {
        return tags;
    }

    public void setTags(List<TagVO> tags) {
        this.tags = tags;
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

    public String getStoragePlatform() {
        return storagePlatform;
    }

    public void setStoragePlatform(String storagePlatform) {
        this.storagePlatform = storagePlatform;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getExtractCode() {
        return extractCode;
    }

    public void setExtractCode(String extractCode) {
        this.extractCode = extractCode;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
