package com.teacherresourcehub.common.enums;

public enum StoragePlatformType {

    BAIDU_PAN("baidu_pan"),
    QUARK_PAN("quark_pan"),
    ALIYUN_PAN("aliyun_pan"),
    TIANYI_PAN("tianyi_pan");

    private final String value;

    StoragePlatformType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
