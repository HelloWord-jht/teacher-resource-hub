package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.ResourceAdminQueryRequest;
import com.teacherresourcehub.dto.ResourceSaveRequest;
import com.teacherresourcehub.dto.StatusUpdateRequest;
import com.teacherresourcehub.service.FulfillmentService;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.vo.AdminResourceListItemVO;
import com.teacherresourcehub.vo.FulfillmentQuickSearchItemVO;
import com.teacherresourcehub.vo.ResourceAdminDetailVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/resources")
public class AdminResourceController {

    private final ResourceService resourceService;
    private final FulfillmentService fulfillmentService;

    public AdminResourceController(ResourceService resourceService, FulfillmentService fulfillmentService) {
        this.resourceService = resourceService;
        this.fulfillmentService = fulfillmentService;
    }

    @GetMapping
    public Result<PageResult<AdminResourceListItemVO>> page(@Valid ResourceAdminQueryRequest request) {
        return Result.success(resourceService.pageAdminResources(request));
    }

    @GetMapping("/{id}")
    public Result<ResourceAdminDetailVO> detail(@PathVariable Long id) {
        return Result.success(resourceService.getAdminResourceDetail(id));
    }

    @GetMapping("/by-code/{resourceCode}")
    public Result<ResourceAdminDetailVO> detailByCode(@PathVariable String resourceCode) {
        return Result.success(resourceService.getAdminResourceDetailByCode(resourceCode));
    }

    @GetMapping("/quick-search")
    public Result<java.util.List<FulfillmentQuickSearchItemVO>> quickSearch(@org.springframework.web.bind.annotation.RequestParam String keyword) {
        return Result.success(fulfillmentService.quickSearch(keyword));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody ResourceSaveRequest request) {
        resourceService.createResource(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ResourceSaveRequest request) {
        resourceService.updateResource(id, request);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        resourceService.updateResourceStatus(id, request.getStatus());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.success();
    }
}
