package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.SiteConfigSaveRequest;
import com.teacherresourcehub.service.SiteConfigService;
import com.teacherresourcehub.vo.SiteConfigVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site-config")
public class AdminSiteConfigController {

    private final SiteConfigService siteConfigService;

    public AdminSiteConfigController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping
    public Result<SiteConfigVO> getConfig() {
        return Result.success(siteConfigService.getSiteConfig());
    }

    @PutMapping
    public Result<Void> save(@Valid @RequestBody SiteConfigSaveRequest request) {
        siteConfigService.saveSiteConfig(request);
        return Result.success();
    }
}
