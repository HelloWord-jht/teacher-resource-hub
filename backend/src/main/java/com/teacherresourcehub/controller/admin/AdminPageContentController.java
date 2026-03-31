package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.PageContentSaveRequest;
import com.teacherresourcehub.service.PageContentService;
import com.teacherresourcehub.vo.PageContentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/page-content")
public class AdminPageContentController {

    private final PageContentService pageContentService;

    public AdminPageContentController(PageContentService pageContentService) {
        this.pageContentService = pageContentService;
    }

    @GetMapping
    public Result<List<PageContentVO>> list() {
        return Result.success(pageContentService.listAll());
    }

    @GetMapping("/{pageCode}")
    public Result<PageContentVO> detail(@PathVariable String pageCode) {
        return Result.success(pageContentService.getByCode(pageCode));
    }

    @PutMapping("/{pageCode}")
    public Result<Void> save(@PathVariable String pageCode, @Valid @RequestBody PageContentSaveRequest request) {
        pageContentService.saveOrUpdate(pageCode, request);
        return Result.success();
    }
}
