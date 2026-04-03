package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.VisitTraceCreateRequest;
import com.teacherresourcehub.entity.VisitTrace;
import com.teacherresourcehub.mapper.VisitTraceMapper;
import com.teacherresourcehub.service.VisitTraceService;
import com.teacherresourcehub.vo.VisitTraceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VisitTraceServiceImpl implements VisitTraceService {

    private final VisitTraceMapper visitTraceMapper;

    public VisitTraceServiceImpl(VisitTraceMapper visitTraceMapper) {
        this.visitTraceMapper = visitTraceMapper;
    }

    @Override
    public void createVisitTrace(VisitTraceCreateRequest request, String ip, String userAgent) {
        VisitTrace trace = new VisitTrace();
        trace.setChannel(request.getChannel() == null ? "" : request.getChannel().trim());
        trace.setTrackingCode(request.getTrackingCode() == null ? "" : request.getTrackingCode().trim());
        trace.setLandingPage(request.getLandingPage().trim());
        trace.setTargetResourceId(request.getTargetResourceId());
        trace.setTargetResourceCode(request.getTargetResourceCode() == null ? "" : request.getTargetResourceCode().trim());
        trace.setClientId(request.getClientId().trim());
        trace.setIp(ip == null ? "" : ip.trim());
        trace.setUserAgent(userAgent == null ? "" : userAgent.trim());
        visitTraceMapper.insert(trace);
    }

    @Override
    public PageResult<VisitTraceVO> page(String channel, String trackingCode, Long pageNum, Long pageSize) {
        Page<VisitTrace> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<VisitTrace> wrapper = new LambdaQueryWrapper<VisitTrace>()
                .orderByDesc(VisitTrace::getCreatedAt, VisitTrace::getId);
        if (StringUtils.hasText(channel)) {
            wrapper.eq(VisitTrace::getChannel, channel.trim());
        }
        if (StringUtils.hasText(trackingCode)) {
            wrapper.eq(VisitTrace::getTrackingCode, trackingCode.trim());
        }
        Page<VisitTrace> result = visitTraceMapper.selectPage(page, wrapper);
        java.util.List<VisitTraceVO> list = result.getRecords().stream().map(item -> {
            VisitTraceVO vo = new VisitTraceVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), list);
    }
}
