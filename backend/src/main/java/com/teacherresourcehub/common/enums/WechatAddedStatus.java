package com.teacherresourcehub.common.enums;

public enum WechatAddedStatus {

    NOT_ADDED(0),
    ADDED(1);

    private final int code;

    WechatAddedStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
