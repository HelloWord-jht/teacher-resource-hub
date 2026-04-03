package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.FulfillmentService;
import com.teacherresourcehub.vo.DeliveryRecordVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/delivery-records")
public class AdminDeliveryRecordController {

    private final FulfillmentService fulfillmentService;

    public AdminDeliveryRecordController(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @GetMapping
    public Result<PageResult<DeliveryRecordVO>> page(@RequestParam(required = false) String resourceCode,
                                                     @RequestParam(defaultValue = "1") Long pageNum,
                                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(fulfillmentService.pageDeliveryRecords(resourceCode, pageNum, pageSize));
    }
}
