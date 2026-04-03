package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.VisitTraceCreateRequest;
import com.teacherresourcehub.vo.VisitTraceVO;
import com.teacherresourcehub.common.api.PageResult;

public interface VisitTraceService {

    void createVisitTrace(VisitTraceCreateRequest request, String ip, String userAgent);

    PageResult<VisitTraceVO> page(String channel, String trackingCode, Long pageNum, Long pageSize);
}
