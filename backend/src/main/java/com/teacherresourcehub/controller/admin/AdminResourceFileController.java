package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.ResourceFileService;
import com.teacherresourcehub.vo.AdminResourceFileDetailVO;
import com.teacherresourcehub.vo.AdminResourceFileItemVO;
import com.teacherresourcehub.vo.AdminResourceFileLogVO;
import com.teacherresourcehub.vo.AdminResourceFilePreviewVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminResourceFileController {

    private final ResourceFileService resourceFileService;

    public AdminResourceFileController(ResourceFileService resourceFileService) {
        this.resourceFileService = resourceFileService;
    }

    @PostMapping("/resources/{resourceId}/files/upload")
    public Result<Void> uploadResourceFile(@PathVariable Long resourceId,
                                           @RequestParam("file") MultipartFile file) {
        resourceFileService.uploadResourceFile(resourceId, file);
        return Result.success();
    }

    @GetMapping("/resources/{resourceId}/files")
    public Result<List<AdminResourceFileItemVO>> listResourceFiles(@PathVariable Long resourceId) {
        return Result.success(resourceFileService.listResourceFiles(resourceId));
    }

    @GetMapping("/resources/{resourceId}/files/{fileId}")
    public Result<AdminResourceFileDetailVO> getResourceFile(@PathVariable Long resourceId,
                                                             @PathVariable Long fileId) {
        return Result.success(resourceFileService.getResourceFileDetail(fileId));
    }

    @PutMapping("/resources/{resourceId}/primary-file/{fileId}")
    public Result<Void> setPrimaryFile(@PathVariable Long resourceId, @PathVariable Long fileId) {
        resourceFileService.setPrimaryFile(resourceId, fileId);
        return Result.success();
    }

    @PostMapping("/import-tasks/{id}/files/upload")
    public Result<Void> uploadImportTaskFile(@PathVariable("id") Long importTaskId,
                                             @RequestParam("file") MultipartFile file) {
        resourceFileService.uploadImportTaskFile(importTaskId, file);
        return Result.success();
    }

    @GetMapping("/import-tasks/{id}/files")
    public Result<List<AdminResourceFileItemVO>> listImportTaskFiles(@PathVariable("id") Long importTaskId) {
        return Result.success(resourceFileService.listImportTaskFiles(importTaskId));
    }

    @PostMapping("/import-tasks/{id}/execute-preview")
    public Result<Void> executePreview(@PathVariable("id") Long importTaskId) {
        resourceFileService.executeImportTaskPreview(importTaskId);
        return Result.success();
    }

    @GetMapping("/resource-files/{fileId}/previews")
    public Result<List<AdminResourceFilePreviewVO>> listFilePreviews(@PathVariable Long fileId) {
        return Result.success(resourceFileService.listFilePreviews(fileId));
    }

    @GetMapping("/resource-files/{fileId}/logs")
    public Result<List<AdminResourceFileLogVO>> listFileLogs(@PathVariable Long fileId) {
        return Result.success(resourceFileService.listFileLogs(fileId));
    }

    @PostMapping("/resource-files/{fileId}/regenerate-preview")
    public Result<Void> regeneratePreview(@PathVariable Long fileId) {
        resourceFileService.regeneratePreview(fileId);
        return Result.success();
    }

    @DeleteMapping("/resource-files/{fileId}")
    public Result<Void> deleteFile(@PathVariable Long fileId) {
        resourceFileService.deleteFile(fileId);
        return Result.success();
    }
}
