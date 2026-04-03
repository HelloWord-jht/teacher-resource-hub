package com.teacherresourcehub.common.enums;

public enum ContentCampaignType {

    NOTE("note"),
    ARTICLE("article"),
    LANDING_PAGE("landing_page"),
    IMAGE_POST("image_post");

    private final String value;

    ContentCampaignType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
