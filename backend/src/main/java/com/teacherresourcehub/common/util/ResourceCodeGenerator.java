package com.teacherresourcehub.common.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ResourceCodeGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ResourceCodeGenerator() {
    }

    public static String generate(String categoryCode, LocalDate date, int sequence) {
        if (!StringUtils.hasText(categoryCode)) {
            throw new IllegalArgumentException("分类缩写不能为空");
        }
        if (date == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (sequence <= 0) {
            throw new IllegalArgumentException("序号必须大于0");
        }
        String normalizedCategoryCode = categoryCode.trim().toUpperCase(Locale.ROOT);
        return "RES-%s-%s-%04d".formatted(normalizedCategoryCode, DATE_FORMATTER.format(date), sequence);
    }
}
