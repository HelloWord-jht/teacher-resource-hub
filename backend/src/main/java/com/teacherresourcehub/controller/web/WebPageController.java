package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.PageContentService;
import com.teacherresourcehub.vo.PageContentVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/pages")
public class WebPageController {

    private final PageContentService pageContentService;

    public WebPageController(PageContentService pageContentService) {
        this.pageContentService = pageContentService;
    }

    @GetMapping("/{pageCode}")
    public Result<PageContentVO> getPage(@PathVariable String pageCode) {
        return Result.success(pageContentService.getByCode(pageCode));
    }
}
