package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlSanitizerTest {

    @Test
    void shouldRemoveScriptTagsButKeepBasicFormatting() {
        String sanitized = HtmlSanitizer.sanitize("""
                <p>适合班主任使用</p>
                <script>alert('xss')</script>
                <ul><li>课件</li><li>教案</li></ul>
                """);

        assertTrue(sanitized.contains("<p>适合班主任使用</p>"));
        assertTrue(sanitized.contains("<ul>"));
        assertFalse(sanitized.contains("<script>"));
        assertFalse(sanitized.contains("alert('xss')"));
    }
}
