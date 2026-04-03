package com.teacherresourcehub.common.util;

import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResourceImportDecisionHelper {

    private static final Map<String, List<String>> CATEGORY_RULES = new LinkedHashMap<>();

    static {
        CATEGORY_RULES.put("JZH", List.of("家长会"));
        CATEGORY_RULES.put("JRZ", List.of("中秋", "国庆", "元旦", "清明", "六一", "劳动节"));
        CATEGORY_RULES.put("BHZ", List.of("班会", "安全教育", "防溺水", "心理健康", "欺凌"));
        CATEGORY_RULES.put("GKK", List.of("公开课", "说课", "示范课"));
        CATEGORY_RULES.put("FXK", List.of("期中", "期末", "复习", "冲刺"));
    }

    private ResourceImportDecisionHelper() {
    }

    public static String recommendCategoryCode(String title, String scene) {
        String searchableText = (normalize(title) + " " + normalize(scene)).trim();
        for (Map.Entry<String, List<String>> entry : CATEGORY_RULES.entrySet()) {
            if (containsAny(searchableText, entry.getValue())) {
                return entry.getKey();
            }
        }
        return "BZR";
    }

    private static boolean containsAny(String text, List<String> keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim();
    }
}
