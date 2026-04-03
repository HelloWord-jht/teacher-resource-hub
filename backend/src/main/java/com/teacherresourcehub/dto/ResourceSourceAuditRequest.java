package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResourceSourceAuditRequest {

    @NotBlank(message = "审核状态不能为空")
    @Size(max = 30, message = "审核状态长度不能超过30个字符")
    private String authorizationStatus;

    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    private String auditRemark;

    @Size(max = 20, message = "风险等级长度不能超过20个字符")
    private String riskLevel;

    public String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public void setAuthorizationStatus(String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}
