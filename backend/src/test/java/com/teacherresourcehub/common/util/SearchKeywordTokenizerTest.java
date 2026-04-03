package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchKeywordTokenizerTest {

    @Test
    void shouldExtractReadableChineseKeywords() {
        List<String> tokens = SearchKeywordTokenizer.tokenize(
                "小学家长会PPT与发言稿通用包，适合班主任快速备课");

        assertFalse(tokens.isEmpty());
        assertTrue(tokens.stream().anyMatch(token -> token.contains("家长会")));
        assertTrue(tokens.stream().anyMatch(token -> token.contains("班主任")));
    }
}
