package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ResourceStorageSaveRequest;
import com.teacherresourcehub.service.ResourceStorageService;
import com.teacherresourcehub.vo.ResourceStorageVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/admin/resource-storages")
public class AdminResourceStorageController {

    private final ResourceStorageService resourceStorageService;

    public AdminResourceStorageController(ResourceStorageService resourceStorageService) {
        this.resourceStorageService = resourceStorageService;
    }

    @GetMapping
    public Result<List<ResourceStorageVO>> list(@RequestParam(required = false) Long resourceId) {
        return Result.success(resourceStorageService.list(resourceId));
    }

    @GetMapping("/{id}")
    public Result<ResourceStorageVO> detail(@PathVariable Long id) {
        return Result.success(resourceStorageService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ResourceStorageSaveRequest request) {
        resourceStorageService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ResourceStorageSaveRequest request) {
        resourceStorageService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceStorageService.delete(id);
        return Result.success();
    }
}
