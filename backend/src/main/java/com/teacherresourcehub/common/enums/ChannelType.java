package com.teacherresourcehub.common.enums;

public enum ChannelType {

    WEBSITE("website"),
    XIAOHONGSHU("xiaohongshu"),
    WECHAT_OFFICIAL("wechat_official"),
    OTHER("other");

    private final String value;

    ChannelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
