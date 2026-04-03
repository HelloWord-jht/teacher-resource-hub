package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.FulfillmentCopyTemplateRequest;
import com.teacherresourcehub.dto.FulfillmentMarkDeliveredRequest;
import com.teacherresourcehub.service.FulfillmentService;
import com.teacherresourcehub.vo.FulfillmentQuickSearchItemVO;
import com.teacherresourcehub.vo.FulfillmentResourceVO;
import com.teacherresourcehub.vo.ResourceSearchLogVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fulfillment")
public class AdminFulfillmentController {

    private final FulfillmentService fulfillmentService;

    public AdminFulfillmentController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @GetMapping("/quick-search")
    public Result<List<FulfillmentQuickSearchItemVO>> quickSearch(@RequestParam String keyword) {
        return Result.success(fulfillmentService.quickSearch(keyword));
    }

    @GetMapping("/resource/{resourceCode}")
    public Result<FulfillmentResourceVO> getResource(@PathVariable String resourceCode) {
        return Result.success(fulfillmentService.getFulfillmentResource(resourceCode));
    }

    @PostMapping("/copy-template")
    public Result<String> copyTemplate(@Valid @RequestBody FulfillmentCopyTemplateRequest request) {
        return Result.success(fulfillmentService.buildDeliveryTemplate(request.getResourceCode()));
    }

    @PostMapping("/mark-delivered")
    public Result<Void> markDelivered(@Valid @RequestBody FulfillmentMarkDeliveredRequest request) {
        fulfillmentService.markDelivered(request);
        return Result.success();
    }

    @GetMapping("/recent-searches")
    public Result<List<ResourceSearchLogVO>> recentSearches(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(fulfillmentService.listRecentSearchLogs(limit));
    }
}
