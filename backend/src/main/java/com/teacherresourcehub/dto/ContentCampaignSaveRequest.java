package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ContentCampaignSaveRequest {

    @NotNull(message = "渠道不能为空")
    private Long channelId;

    @NotBlank(message = "投放标题不能为空")
    @Size(max = 150, message = "投放标题长度不能超过150个字符")
    private String title;

    @NotBlank(message = "内容类型不能为空")
    @Size(max = 30, message = "内容类型长度不能超过30个字符")
    private String contentType;

    private LocalDateTime publishTime;

    private Long targetResourceId;

    @Size(max = 50, message = "目标资源码长度不能超过50个字符")
    private String targetResourceCode;

    @Size(max = 255, message = "落地页长度不能超过255个字符")
    private String landingPage;

    @NotBlank(message = "追踪码不能为空")
    @Size(max = 100, message = "追踪码长度不能超过100个字符")
    private String trackingCode;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    @NotNull(message = "状态不能为空")
    private Integer status;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
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

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
