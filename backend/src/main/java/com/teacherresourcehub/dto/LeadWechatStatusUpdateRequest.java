package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LeadWechatStatusUpdateRequest {

    @NotNull(message = "加微信状态不能为空")
    private Integer wechatAddedStatus;

    @Size(max = 1000, message = "跟进备注长度不能超过1000个字符")
    private String followUpNote;

    public Integer getWechatAddedStatus() {
        return wechatAddedStatus;
    }

    public void setWechatAddedStatus(Integer wechatAddedStatus) {
        this.wechatAddedStatus = wechatAddedStatus;
    }

    public String getFollowUpNote() {
        return followUpNote;
    }

    public void setFollowUpNote(String followUpNote) {
        this.followUpNote = followUpNote;
    }
}
