package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeadCreateRequest {

    @NotBlank(message = "老师称呼不能为空")
    @Size(max = 50, message = "老师称呼长度不能超过50个字符")
    private String name;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式长度不能超过100个字符")
    private String contact;

    @NotBlank(message = "来源页面不能为空")
    @Size(max = 120, message = "来源页面长度不能超过120个字符")
    private String sourcePage;

    @Size(max = 1000, message = "咨询内容长度不能超过1000个字符")
    private String message;

    @Size(max = 50, message = "渠道长度不能超过50个字符")
    private String channel;

    @Size(max = 100, message = "追踪码长度不能超过100个字符")
    private String trackingCode;

    private Long targetResourceId;

    @Size(max = 50, message = "资源码长度不能超过50个字符")
    private String targetResourceCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public Long getTargetResourceId() {
        return targetResourceId;
    }

    public void setTargetResourceId(Long targetResourceId) {
        this.targetResourceId = targetResourceId;
    }

    public String getTargetResourceCode() {
        return targetResourceCode;
    }

    public void setTargetResourceCode(String targetResourceCode) {
        this.targetResourceCode = targetResourceCode;
    }
}
