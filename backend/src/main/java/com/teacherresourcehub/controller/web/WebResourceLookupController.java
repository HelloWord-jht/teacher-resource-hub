package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.vo.ResourceDetailVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web")
public class WebResourceLookupController {

    private final ResourceService resourceService;

    public WebResourceLookupController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/resource-by-code/{resourceCode}")
    public Result<ResourceDetailVO> detailByCode(@PathVariable String resourceCode) {
        return Result.success(resourceService.getWebResourceDetailByCode(resourceCode));
    }
}
