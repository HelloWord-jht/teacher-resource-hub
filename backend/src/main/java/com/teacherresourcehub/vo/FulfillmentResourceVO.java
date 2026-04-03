package com.teacherresourcehub.vo;

import java.util.List;

public class FulfillmentResourceVO {

    private Long resourceId;
    private String title;
    private String resourceCode;
    private String scene;
    private String categoryName;
    private String authorizationStatusSnapshot;
    private Integer status;
    private String storagePlatform;
    private String shareUrl;
    private String shareCode;
    private String extractCode;
    private String deliveryNote;
    private String deliveryTemplate;
    private List<DeliveryRecordVO> recentDeliveryRecords;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getDeliveryNote() {
        return deliveryNote;
    }

    public void setDeliveryNote(String deliveryNote) {
        this.deliveryNote = deliveryNote;
    }

    public String getDeliveryTemplate() {
        return deliveryTemplate;
    }

    public void setDeliveryTemplate(String deliveryTemplate) {
        this.deliveryTemplate = deliveryTemplate;
    }

    public List<DeliveryRecordVO> getRecentDeliveryRecords() {
        return recentDeliveryRecords;
    }

    public void setRecentDeliveryRecords(List<DeliveryRecordVO> recentDeliveryRecords) {
        this.recentDeliveryRecords = recentDeliveryRecords;
    }
}
