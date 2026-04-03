package com.teacherresourcehub.common.enums;

public enum ResourceFileSourceType {

    UPLOAD("upload"),
    EXTRACTED("extracted");

    private final String code;

    ResourceFileSourceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
