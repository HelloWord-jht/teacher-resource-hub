package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.CategoryService;
import com.teacherresourcehub.service.TagService;
import com.teacherresourcehub.vo.CategoryVO;
import com.teacherresourcehub.vo.TagVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/web")
public class WebTaxonomyController {

    private final CategoryService categoryService;
    private final TagService tagService;

    public WebTaxonomyController(CategoryService categoryService, TagService tagService) {
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @GetMapping("/categories")
    public Result<List<CategoryVO>> categories() {
        return Result.success(categoryService.listEnabledCategories());
    }

    @GetMapping("/tags")
    public Result<List<TagVO>> tags() {
        return Result.success(tagService.listEnabledTags());
    }
}
