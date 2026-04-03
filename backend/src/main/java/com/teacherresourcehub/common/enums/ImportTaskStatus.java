package com.teacherresourcehub.common.enums;

public enum ImportTaskStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    PARTIAL_SUCCESS("partial_success"),
    SUCCESS("success"),
    FAILED("failed");

    private final String code;

    ImportTaskStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
