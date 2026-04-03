package com.teacherresourcehub.common.enums;

import java.util.Arrays;

public enum ResourceFileDetectedType {

    PPT("ppt"),
    PPTX("pptx"),
    DOC("doc"),
    DOCX("docx"),
    PDF("pdf"),
    IMAGE("image"),
    ZIP("zip"),
    TXT("txt"),
    MD("md"),
    XLS("xls"),
    XLSX("xlsx"),
    UNKNOWN("unknown");

    private final String code;

    ResourceFileDetectedType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isPreviewSupportedInCurrentVersion() {
        return this == IMAGE || this == PDF || this == TXT || this == MD
                || this == PPT || this == PPTX || this == DOC || this == DOCX;
    }

    public boolean isPreferredPreviewType() {
        return this == PPT || this == PPTX || this == PDF || this == DOC || this == DOCX;
    }

    public static ResourceFileDetectedType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return UNKNOWN;
        }
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
