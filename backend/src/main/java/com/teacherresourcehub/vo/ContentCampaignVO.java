package com.teacherresourcehub.vo;

import java.time.LocalDateTime;

public class ContentCampaignVO {

    private Long id;
    private Long channelId;
    private String channelName;
    private String title;
    private String contentType;
    private LocalDateTime publishTime;
    private Long targetResourceId;
    private String targetResourceCode;
    private String landingPage;
    private String trackingCode;
    private Integer viewCount;
    private Integer consultCount;
    private Integer wechatAddCount;
    private Integer dealCount;
    private String remark;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public Long getTargetResourceId() { return targetResourceId; }
    public void setTargetResourceId(Long targetResourceId) { this.targetResourceId = targetResourceId; }
    public String getTargetResourceCode() { return targetResourceCode; }
    public void setTargetResourceCode(String targetResourceCode) { this.targetResourceCode = targetResourceCode; }
    public String getLandingPage() { return landingPage; }
    public void setLandingPage(String landingPage) { this.landingPage = landingPage; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getConsultCount() { return consultCount; }
    public void setConsultCount(Integer consultCount) { this.consultCount = consultCount; }
    public Integer getWechatAddCount() { return wechatAddCount; }
    public void setWechatAddCount(Integer wechatAddCount) { this.wechatAddCount = wechatAddCount; }
    public Integer getDealCount() { return dealCount; }
    public void setDealCount(Integer dealCount) { this.dealCount = dealCount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
