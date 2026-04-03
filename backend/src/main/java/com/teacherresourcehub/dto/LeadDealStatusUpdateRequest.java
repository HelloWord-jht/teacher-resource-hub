package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LeadDealStatusUpdateRequest {

    @NotNull(message = "成交状态不能为空")
    private Integer dealStatus;

    @Size(max = 1000, message = "跟进备注长度不能超过1000个字符")
    private String followUpNote;

    public Integer getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(Integer dealStatus) {
        this.dealStatus = dealStatus;
    }

    public String getFollowUpNote() {
        return followUpNote;
    }

    public void setFollowUpNote(String followUpNote) {
        this.followUpNote = followUpNote;
    }
}
