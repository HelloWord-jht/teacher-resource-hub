package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.LeadStatusUpdateRequest;
import com.teacherresourcehub.service.LeadService;
import com.teacherresourcehub.vo.LeadVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/leads")
public class AdminLeadController {

    private final LeadService leadService;

    public AdminLeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public Result<PageResult<LeadVO>> page(@RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(leadService.pageAdminLeads(status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<LeadVO> detail(@PathVariable Long id) {
        return Result.success(leadService.getLeadDetail(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody LeadStatusUpdateRequest request) {
        leadService.updateLeadStatus(id, request);
        return Result.success();
    }
}
