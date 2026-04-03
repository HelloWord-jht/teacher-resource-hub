package com.teacherresourcehub.common.enums;

public enum LeadDealStatus {

    NOT_DEAL(0),
    DEAL(1),
    CLOSED(2);

    private final int code;

    LeadDealStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
