package com.teacherresourcehub.common.enums;

public enum ResourceSourceType {

    SELF_COMPILED("self_compiled"),
    COOPERATION("cooperation"),
    SUBMISSION("submission"),
    CURATED("curated");

    private final String value;

    ResourceSourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
