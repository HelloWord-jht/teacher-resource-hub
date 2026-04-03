package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.VisitTraceService;
import com.teacherresourcehub.vo.VisitTraceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/visit-traces")
public class AdminVisitTraceController {

    private final VisitTraceService visitTraceService;

    public AdminVisitTraceController(VisitTraceService visitTraceService) {
        this.visitTraceService = visitTraceService;
    }

    @GetMapping
    public Result<PageResult<VisitTraceVO>> page(@RequestParam(required = false) String channel,
                                                 @RequestParam(required = false) String trackingCode,
                                                 @RequestParam(defaultValue = "1") Long pageNum,
                                                 @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(visitTraceService.page(channel, trackingCode, pageNum, pageSize));
    }
}
