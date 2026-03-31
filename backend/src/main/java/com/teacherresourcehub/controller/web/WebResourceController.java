package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ResourceQueryRequest;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.vo.ResourceDetailVO;
import com.teacherresourcehub.vo.ResourceListItemVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/resources")
public class WebResourceController {

    private final ResourceService resourceService;

    public WebResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public Result<PageResult<ResourceListItemVO>> page(@Valid ResourceQueryRequest request) {
        return Result.success(resourceService.pageWebResources(request));
    }

    @GetMapping("/{id}")
    public Result<ResourceDetailVO> detail(@PathVariable Long id) {
        return Result.success(resourceService.getWebResourceDetail(id));
    }
}
