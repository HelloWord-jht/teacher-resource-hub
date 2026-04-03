package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ContentCampaignSaveRequest;
import com.teacherresourcehub.service.ContentCampaignService;
import com.teacherresourcehub.vo.ContentCampaignVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content-campaigns")
public class AdminContentCampaignController {

    private final ContentCampaignService contentCampaignService;

    public AdminContentCampaignController(ContentCampaignService contentCampaignService) {
        this.contentCampaignService = contentCampaignService;
    }

    @GetMapping
    public Result<List<ContentCampaignVO>> list(@RequestParam(required = false) Long channelId) {
        return Result.success(contentCampaignService.list(channelId));
    }

    @GetMapping("/{id}")
    public Result<ContentCampaignVO> detail(@PathVariable Long id) {
        return Result.success(contentCampaignService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ContentCampaignSaveRequest request) {
        contentCampaignService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ContentCampaignSaveRequest request) {
        contentCampaignService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        contentCampaignService.delete(id);
        return Result.success();
    }
}
