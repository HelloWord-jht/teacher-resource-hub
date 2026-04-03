package com.teacherresourcehub.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceImportDecisionHelperTest {

    @Test
    void shouldRecommendParentMeetingCategoryForJiaZhangHuiKeyword() {
        assertEquals("JZH", ResourceImportDecisionHelper.recommendCategoryCode("小学家长会 PPT 通用包", "家校沟通"));
    }

    @Test
    void shouldRecommendClassMeetingCategoryForSafetyTopics() {
        assertEquals("BHZ", ResourceImportDecisionHelper.recommendCategoryCode("防溺水安全教育专题包", "安全教育"));
    }

    @Test
    void shouldRecommendOpenClassCategoryForOpenClassKeywords() {
        assertEquals("GKK", ResourceImportDecisionHelper.recommendCategoryCode("小学语文公开课模板包", "公开课"));
    }

    @Test
    void shouldRecommendReviewCategoryForReviewKeywords() {
        assertEquals("FXK", ResourceImportDecisionHelper.recommendCategoryCode("小学期末冲刺复习资料包", "期末复习"));
    }

    @Test
    void shouldRecommendFestivalCategoryForFestivalKeywords() {
        assertEquals("JRZ", ResourceImportDecisionHelper.recommendCategoryCode("六一儿童节班会活动包", "节日活动"));
    }

    @Test
    void shouldFallbackToHeadTeacherCategory() {
        assertEquals("BZR", ResourceImportDecisionHelper.recommendCategoryCode("小学班级常规管理模板", "班级管理"));
    }
}
