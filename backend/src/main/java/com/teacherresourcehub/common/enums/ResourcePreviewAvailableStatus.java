package com.teacherresourcehub.common.enums;

public enum ResourcePreviewAvailableStatus {

    NONE("none"),
    PARTIAL("partial"),
    AVAILABLE("available");

    private final String code;

    ResourcePreviewAvailableStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
