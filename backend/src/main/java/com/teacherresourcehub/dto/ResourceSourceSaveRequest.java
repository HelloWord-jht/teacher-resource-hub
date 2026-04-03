package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResourceSourceSaveRequest {

    @NotBlank(message = "来源名称不能为空")
    @Size(max = 100, message = "来源名称长度不能超过100个字符")
    private String sourceName;

    @NotBlank(message = "来源类型不能为空")
    @Size(max = 30, message = "来源类型长度不能超过30个字符")
    private String sourceType;

    @Size(max = 100, message = "来源方名称长度不能超过100个字符")
    private String ownerName;

    @Size(max = 100, message = "联系方式长度不能超过100个字符")
    private String ownerContact;

    @Size(max = 255, message = "证明材料地址长度不能超过255个字符")
    private String authorizationProofUrl;

    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    private String auditRemark;

    @NotNull(message = "状态不能为空")
    private Integer status;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public String getAuthorizationProofUrl() {
        return authorizationProofUrl;
    }

    public void setAuthorizationProofUrl(String authorizationProofUrl) {
        this.authorizationProofUrl = authorizationProofUrl;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
