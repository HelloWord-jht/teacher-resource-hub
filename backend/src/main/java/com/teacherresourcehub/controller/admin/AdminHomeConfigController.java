package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.HomeConfigSaveRequest;
import com.teacherresourcehub.service.SiteConfigService;
import com.teacherresourcehub.vo.HomeConfigVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/home-config")
public class AdminHomeConfigController {

    private final SiteConfigService siteConfigService;

    public AdminHomeConfigController(SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    @GetMapping
    public Result<HomeConfigVO> getConfig() {
        return Result.success(siteConfigService.getHomeConfig());
    }

    @PutMapping
    public Result<Void> save(@Valid @RequestBody HomeConfigSaveRequest request) {
        siteConfigService.saveHomeConfig(request);
        return Result.success();
    }
}
