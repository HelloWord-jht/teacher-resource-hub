package com.teacherresourcehub.common.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public final class HtmlSanitizer {

    private static final Safelist BASIC_SAFE_LIST = Safelist.relaxed()
            .addTags("section", "article", "figure", "figcaption")
            .addAttributes(":all", "class")
            .addProtocols("img", "src", "http", "https");

    private HtmlSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, BASIC_SAFE_LIST);
    }
}
