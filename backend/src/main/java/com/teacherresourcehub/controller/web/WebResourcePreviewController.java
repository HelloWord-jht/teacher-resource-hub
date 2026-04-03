package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.ResourceFileQueryService;
import com.teacherresourcehub.vo.WebResourcePreviewFileVO;
import com.teacherresourcehub.vo.WebResourcePreviewPageVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/web/resources")
public class WebResourcePreviewController {

    private final ResourceFileQueryService resourceFileQueryService;

    public WebResourcePreviewController(ResourceFileQueryService resourceFileQueryService) {
        this.resourceFileQueryService = resourceFileQueryService;
    }

    @GetMapping("/{resourceId}/preview-files")
    public Result<List<WebResourcePreviewFileVO>> listPreviewFiles(@PathVariable Long resourceId) {
        return Result.success(resourceFileQueryService.listWebPreviewFiles(resourceId));
    }

    @GetMapping("/{resourceId}/preview-files/{fileId}/previews")
    public Result<List<WebResourcePreviewPageVO>> listPreviewPages(@PathVariable Long resourceId,
                                                                   @PathVariable Long fileId) {
        return Result.success(resourceFileQueryService.listWebPreviewPages(resourceId, fileId));
    }
}
