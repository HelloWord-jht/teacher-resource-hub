package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.LeadCreateRequest;
import com.teacherresourcehub.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/leads")
public class WebLeadController {

    private final LeadService leadService;

    public WebLeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody LeadCreateRequest request) {
        leadService.createLead(request);
        return Result.success("提交成功，我们会尽快联系您", null);
    }
}
