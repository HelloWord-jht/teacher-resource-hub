package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FulfillmentTemplateBuilderTest {

    @Test
    void shouldBuildCompleteDeliveryTemplate() {
        String content = FulfillmentTemplateBuilder.build(
                "小学家长会 PPT + 发言稿通用包",
                "RES-JZH-20260403-0001",
                "https://example.com/pan/res-jzh-20260403-0001",
                "JZH001",
                "2244",
                "如链接失效请及时联系补发"
        );

        assertEquals("""
                资源名称：小学家长会 PPT + 发言稿通用包
                资源码：RES-JZH-20260403-0001
                网盘链接：https://example.com/pan/res-jzh-20260403-0001
                分享码：JZH001
                提取码：2244
                说明：如链接失效请及时联系补发
                """, content);
    }

    @Test
    void shouldKeepOptionalFieldsEmptyInsteadOfNullText() {
        String content = FulfillmentTemplateBuilder.build(
                "小学开学第一课课件教案包",
                "RES-BZR-20260403-0001",
                "https://example.com/pan/res-bzr-20260403-0001",
                null,
                "",
                null
        );

        assertEquals("""
                资源名称：小学开学第一课课件教案包
                资源码：RES-BZR-20260403-0001
                网盘链接：https://example.com/pan/res-bzr-20260403-0001
                分享码：
                提取码：
                说明：
                """, content);
    }
}
