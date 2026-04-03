package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LeadChannelSaveRequest {

    @NotBlank(message = "渠道标识不能为空")
    @Size(max = 50, message = "渠道标识长度不能超过50个字符")
    private String channelKey;

    @NotBlank(message = "渠道名称不能为空")
    @Size(max = 50, message = "渠道名称长度不能超过50个字符")
    private String channelName;

    @NotBlank(message = "渠道类型不能为空")
    @Size(max = 30, message = "渠道类型长度不能超过30个字符")
    private String channelType;

    @Size(max = 255, message = "渠道说明长度不能超过255个字符")
    private String description;

    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;

    @NotNull(message = "状态不能为空")
    private Integer status;

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
