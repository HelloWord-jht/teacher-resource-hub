package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VisitTraceCreateRequest {

    @Size(max = 50, message = "渠道长度不能超过50个字符")
    private String channel;

    @Size(max = 100, message = "追踪码长度不能超过100个字符")
    private String trackingCode;

    @NotBlank(message = "落地页不能为空")
    @Size(max = 255, message = "落地页长度不能超过255个字符")
    private String landingPage;

    private Long targetResourceId;

    @Size(max = 50, message = "资源码长度不能超过50个字符")
    private String targetResourceCode;

    @NotBlank(message = "客户端标识不能为空")
    @Size(max = 100, message = "客户端标识长度不能超过100个字符")
    private String clientId;

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

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
