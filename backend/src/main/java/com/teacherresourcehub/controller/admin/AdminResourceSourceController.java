package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ResourceSourceAuditRequest;
import com.teacherresourcehub.dto.ResourceSourceSaveRequest;
import com.teacherresourcehub.service.ResourceSourceService;
import com.teacherresourcehub.vo.ResourceSourceVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/resource-sources")
public class AdminResourceSourceController {

    private final ResourceSourceService resourceSourceService;

    public AdminResourceSourceController(ResourceSourceService resourceSourceService) {
        this.resourceSourceService = resourceSourceService;
    }

    @GetMapping
    public Result<List<ResourceSourceVO>> list(@RequestParam(required = false) String authorizationStatus) {
        return Result.success(resourceSourceService.list(authorizationStatus));
    }

    @GetMapping("/{id}")
    public Result<ResourceSourceVO> detail(@PathVariable Long id) {
        return Result.success(resourceSourceService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ResourceSourceSaveRequest request) {
        resourceSourceService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ResourceSourceSaveRequest request) {
        resourceSourceService.update(id, request);
        return Result.success();
    }

    @PutMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody ResourceSourceAuditRequest request) {
        resourceSourceService.audit(id, request);
        return Result.success();
    }
}
