package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.LeadCreateRequest;
import com.teacherresourcehub.dto.LeadStatusUpdateRequest;
import com.teacherresourcehub.entity.Lead;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.LeadMapper;
import com.teacherresourcehub.service.LeadService;
import com.teacherresourcehub.vo.DashboardRecentLeadVO;
import com.teacherresourcehub.vo.LeadVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadMapper leadMapper;

    public LeadServiceImpl(LeadMapper leadMapper) {
        this.leadMapper = leadMapper;
    }

    @Override
    public void createLead(LeadCreateRequest request) {
        Lead lead = new Lead();
        BeanUtils.copyProperties(request, lead);
        if (lead.getMessage() == null) {
            lead.setMessage("");
        }
        lead.setStatus(0);
        lead.setFollowUpNote("");
        leadMapper.insert(lead);
    }

    @Override
    public PageResult<LeadVO> pageAdminLeads(Integer status, Long pageNum, Long pageSize) {
        Page<Lead> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<Lead>()
                .orderByDesc(Lead::getCreatedAt, Lead::getId);
        if (status != null) {
            wrapper.eq(Lead::getStatus, status);
        }
        Page<Lead> result = leadMapper.selectPage(page, wrapper);
        List<LeadVO> list = result.getRecords().stream().map(this::toLeadVO).toList();
        return PageResult.of(result.getCurrent(), result.getSize(), result.getTotal(), list);
    }

    @Override
    public LeadVO getLeadDetail(Long id) {
        return toLeadVO(getLeadOrThrow(id));
    }

    @Override
    public void updateLeadStatus(Long id, LeadStatusUpdateRequest request) {
        Lead lead = getLeadOrThrow(id);
        lead.setStatus(request.getStatus());
        lead.setFollowUpNote(request.getFollowUpNote() == null ? "" : request.getFollowUpNote());
        leadMapper.updateById(lead);
    }

    @Override
    public List<DashboardRecentLeadVO> listRecentLeads(int limit) {
        return leadMapper.selectList(new LambdaQueryWrapper<Lead>()
                .orderByDesc(Lead::getCreatedAt, Lead::getId)
                .last("LIMIT " + limit))
                .stream()
                .map(this::toDashboardRecentLeadVO)
                .toList();
    }

    @Override
    public long countTotal() {
        Long count = leadMapper.selectCount(new LambdaQueryWrapper<Lead>());
        return count == null ? 0L : count;
    }

    private Lead getLeadOrThrow(Long id) {
        Lead lead = leadMapper.selectById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        return lead;
    }

    private LeadVO toLeadVO(Lead lead) {
        LeadVO vo = new LeadVO();
        BeanUtils.copyProperties(lead, vo);
        return vo;
    }

    private DashboardRecentLeadVO toDashboardRecentLeadVO(Lead lead) {
        DashboardRecentLeadVO vo = new DashboardRecentLeadVO();
        BeanUtils.copyProperties(lead, vo);
        return vo;
    }
}
