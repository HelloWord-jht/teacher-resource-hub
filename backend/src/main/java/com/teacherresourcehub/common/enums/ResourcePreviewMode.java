package com.teacherresourcehub.common.enums;

public enum ResourcePreviewMode {

    SINGLE("single"),
    MULTI("multi"),
    ZIP_BUNDLE("zip_bundle");

    private final String code;

    ResourcePreviewMode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
