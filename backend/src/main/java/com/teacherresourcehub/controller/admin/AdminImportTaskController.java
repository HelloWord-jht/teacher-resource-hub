package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ImportTaskSaveRequest;
import com.teacherresourcehub.service.ImportTaskService;
import com.teacherresourcehub.vo.ImportTaskVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/import-tasks")
public class AdminImportTaskController {

    private final ImportTaskService importTaskService;

    public AdminImportTaskController(ImportTaskService importTaskService) {
        this.importTaskService = importTaskService;
    }

    @GetMapping
    public Result<PageResult<ImportTaskVO>> page(@RequestParam(required = false) String importStatus,
                                                 @RequestParam(defaultValue = "1") Long pageNum,
                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(importTaskService.page(importStatus, pageNum, pageSize));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ImportTaskSaveRequest request) {
        importTaskService.create(request);
        return Result.success();
    }

    @PostMapping("/{id}/execute")
    public Result<Void> execute(@PathVariable Long id) {
        importTaskService.execute(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ImportTaskVO> detail(@PathVariable Long id) {
        return Result.success(importTaskService.getById(id));
    }
}
