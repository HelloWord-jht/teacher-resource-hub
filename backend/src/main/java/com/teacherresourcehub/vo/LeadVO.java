package com.teacherresourcehub.vo;

import java.time.LocalDateTime;

public class LeadVO {

    private Long id;
    private String name;
    private String contact;
    private String sourcePage;
    private String message;
    private Integer status;
    private String followUpNote;
    private String channel;
    private String trackingCode;
    private Long targetResourceId;
    private String targetResourceCode;
    private Integer wechatAddedStatus;
    private Integer dealStatus;
    private LocalDateTime lastFollowUpTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFollowUpNote() {
        return followUpNote;
    }

    public void setFollowUpNote(String followUpNote) {
        this.followUpNote = followUpNote;
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

    public Integer getWechatAddedStatus() {
        return wechatAddedStatus;
    }

    public void setWechatAddedStatus(Integer wechatAddedStatus) {
        this.wechatAddedStatus = wechatAddedStatus;
    }

    public Integer getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(Integer dealStatus) {
        this.dealStatus = dealStatus;
    }

    public LocalDateTime getLastFollowUpTime() {
        return lastFollowUpTime;
    }

    public void setLastFollowUpTime(LocalDateTime lastFollowUpTime) {
        this.lastFollowUpTime = lastFollowUpTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
