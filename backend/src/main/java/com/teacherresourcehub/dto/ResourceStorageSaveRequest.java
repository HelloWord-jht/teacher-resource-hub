package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResourceStorageSaveRequest {

    @NotNull(message = "资源不能为空")
    private Long resourceId;

    @NotBlank(message = "网盘平台不能为空")
    @Size(max = 50, message = "网盘平台长度不能超过50个字符")
    private String storagePlatform;

    @NotBlank(message = "分享链接不能为空")
    @Size(max = 500, message = "分享链接长度不能超过500个字符")
    private String shareUrl;

    @Size(max = 100, message = "分享码长度不能超过100个字符")
    private String shareCode;

    @Size(max = 100, message = "提取码长度不能超过100个字符")
    private String extractCode;

    @Size(max = 500, message = "交付备注长度不能超过500个字符")
    private String deliveryNote;

    @NotNull(message = "状态不能为空")
    private Integer status;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
