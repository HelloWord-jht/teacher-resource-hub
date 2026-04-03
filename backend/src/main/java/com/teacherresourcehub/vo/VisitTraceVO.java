package com.teacherresourcehub.vo;

import java.time.LocalDateTime;

public class VisitTraceVO {

    private Long id;
    private String channel;
    private String trackingCode;
    private String landingPage;
    private Long targetResourceId;
    private String targetResourceCode;
    private String clientId;
    private String ip;
    private String userAgent;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public String getLandingPage() { return landingPage; }
    public void setLandingPage(String landingPage) { this.landingPage = landingPage; }
    public Long getTargetResourceId() { return targetResourceId; }
    public void setTargetResourceId(Long targetResourceId) { this.targetResourceId = targetResourceId; }
    public String getTargetResourceCode() { return targetResourceCode; }
    public void setTargetResourceCode(String targetResourceCode) { this.targetResourceCode = targetResourceCode; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
