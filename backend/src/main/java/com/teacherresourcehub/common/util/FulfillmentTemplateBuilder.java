package com.teacherresourcehub.common.util;

public final class FulfillmentTemplateBuilder {

    private FulfillmentTemplateBuilder() {
    }

    public static String build(String resourceTitle,
                               String resourceCode,
                               String shareUrl,
                               String shareCode,
                               String extractCode,
                               String note) {
        return """
                资源名称：%s
                资源码：%s
                网盘链接：%s
                分享码：%s
                提取码：%s
                说明：%s
                """.formatted(
                safeValue(resourceTitle),
                safeValue(resourceCode),
                safeValue(shareUrl),
                safeValue(shareCode),
                safeValue(extractCode),
                safeValue(note)
        );
    }

    private static String safeValue(String value) {
        return value == null ? "" : value.trim();
    }
}
