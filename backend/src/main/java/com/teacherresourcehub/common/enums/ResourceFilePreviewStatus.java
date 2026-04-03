package com.teacherresourcehub.common.enums;

import java.util.Arrays;

public enum ResourceFilePreviewStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    SUCCESS("success"),
    FAILED("failed"),
    UNSUPPORTED("unsupported");

    private final String code;

    ResourceFilePreviewStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ResourceFilePreviewStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            return PENDING;
        }
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(PENDING);
    }
}
