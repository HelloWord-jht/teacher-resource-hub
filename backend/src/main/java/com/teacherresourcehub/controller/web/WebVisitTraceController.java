package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.VisitTraceCreateRequest;
import com.teacherresourcehub.service.VisitTraceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/visit-trace")
public class WebVisitTraceController {

    private final VisitTraceService visitTraceService;

    public WebVisitTraceController(VisitTraceService visitTraceService) {
        this.visitTraceService = visitTraceService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody VisitTraceCreateRequest request, HttpServletRequest httpServletRequest) {
        visitTraceService.createVisitTrace(
                request,
                resolveIp(httpServletRequest),
                httpServletRequest.getHeader("User-Agent")
        );
        return Result.success();
    }

    private String resolveIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
