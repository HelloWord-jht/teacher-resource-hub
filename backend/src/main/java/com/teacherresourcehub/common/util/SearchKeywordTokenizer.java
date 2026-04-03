package com.teacherresourcehub.common.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SearchKeywordTokenizer {

    private static final JiebaSegmenter JIEBA_SEGMENTER = new JiebaSegmenter();
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("[\\p{IsHan}A-Za-z0-9]{2,}");

    private SearchKeywordTokenizer() {
    }

    public static List<String> tokenize(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }

        Set<String> tokens = new LinkedHashSet<>();
        try {
            JIEBA_SEGMENTER.sentenceProcess(text).stream()
                    .map(String::trim)
                    .filter(token -> token.length() >= 2)
                    .forEach(tokens::add);
        } catch (Exception ignore) {
            // 保留后备分词逻辑，避免分词组件异常时影响导入与搜索。
        }

        Matcher matcher = SIMPLE_PATTERN.matcher(text);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return new ArrayList<>(tokens);
    }

    public static String joinAsSearchKeywords(String... texts) {
        Set<String> tokens = new LinkedHashSet<>();
        if (texts != null) {
            for (String text : texts) {
                tokens.addAll(tokenize(text));
            }
        }
        return String.join(",", tokens);
    }
}
