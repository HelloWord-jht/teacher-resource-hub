package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FulfillmentMarkDeliveredRequest {

    @NotBlank(message = "资源码不能为空")
    @Size(max = 50, message = "资源码长度不能超过50个字符")
    private String resourceCode;

    private Long leadId;

    @Size(max = 500, message = "发货备注长度不能超过500个字符")
    private String deliveryRemark;

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public String getDeliveryRemark() {
        return deliveryRemark;
    }

    public void setDeliveryRemark(String deliveryRemark) {
        this.deliveryRemark = deliveryRemark;
    }
}
