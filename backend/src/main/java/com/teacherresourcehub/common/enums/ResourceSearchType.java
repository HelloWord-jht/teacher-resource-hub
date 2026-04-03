package com.teacherresourcehub.common.enums;

public enum ResourceSearchType {

    RESOURCE_CODE("resource_code"),
    TITLE("title"),
    SLUG("slug"),
    ID("id"),
    KEYWORD("keyword");

    private final String value;

    ResourceSearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
