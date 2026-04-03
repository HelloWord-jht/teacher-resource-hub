package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FulfillmentCopyTemplateRequest {

    @NotBlank(message = "资源码不能为空")
    @Size(max = 50, message = "资源码长度不能超过50个字符")
    private String resourceCode;

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }
}
