package com.teacherresourcehub.common.enums;

public enum DeliveryChannelType {

    WECHAT("wechat");

    private final String value;

    DeliveryChannelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
