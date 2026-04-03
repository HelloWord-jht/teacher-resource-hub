package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.LeadDealStatusUpdateRequest;
import com.teacherresourcehub.dto.LeadCreateRequest;
import com.teacherresourcehub.dto.LeadStatusUpdateRequest;
import com.teacherresourcehub.dto.LeadWechatStatusUpdateRequest;
import com.teacherresourcehub.entity.Lead;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.LeadMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.LeadService;
import com.teacherresourcehub.vo.DashboardRecentLeadVO;
import com.teacherresourcehub.vo.LeadVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadMapper leadMapper;
    private final ResourceMapper resourceMapper;

    public LeadServiceImpl(LeadMapper leadMapper, ResourceMapper resourceMapper) {
        this.leadMapper = leadMapper;
        this.resourceMapper = resourceMapper;
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
        lead.setChannel(request.getChannel() == null ? "" : request.getChannel().trim());
        lead.setTrackingCode(request.getTrackingCode() == null ? "" : request.getTrackingCode().trim());
        lead.setTargetResourceId(request.getTargetResourceId());
        lead.setTargetResourceCode(request.getTargetResourceCode() == null ? "" : request.getTargetResourceCode().trim());
        lead.setWechatAddedStatus(0);
        lead.setDealStatus(0);
        lead.setLastFollowUpTime(null);
        leadMapper.insert(lead);
        increaseConsultCount(request.getTargetResourceId());
    }

    @Override
    public PageResult<LeadVO> pageAdminLeads(Integer status,
                                             String channel,
                                             Integer wechatAddedStatus,
                                             Integer dealStatus,
                                             String keyword,
                                             String targetResourceCode,
                                             Long pageNum,
                                             Long pageSize) {
        Page<Lead> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<Lead>()
                .orderByDesc(Lead::getCreatedAt, Lead::getId);
        if (status != null) {
            wrapper.eq(Lead::getStatus, status);
        }
        if (StringUtils.hasText(channel)) {
            wrapper.eq(Lead::getChannel, channel.trim());
        }
        if (wechatAddedStatus != null) {
            wrapper.eq(Lead::getWechatAddedStatus, wechatAddedStatus);
        }
        if (dealStatus != null) {
            wrapper.eq(Lead::getDealStatus, dealStatus);
        }
        if (StringUtils.hasText(targetResourceCode)) {
            wrapper.eq(Lead::getTargetResourceCode, targetResourceCode.trim());
        }
        if (StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim();
            wrapper.and(w -> w.like(Lead::getName, trimmedKeyword)
                    .or()
                    .like(Lead::getContact, trimmedKeyword)
                    .or()
                    .like(Lead::getMessage, trimmedKeyword)
                    .or()
                    .like(Lead::getTrackingCode, trimmedKeyword)
                    .or()
                    .like(Lead::getTargetResourceCode, trimmedKeyword));
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
        lead.setLastFollowUpTime(LocalDateTime.now());
        leadMapper.updateById(lead);
    }

    @Override
    public void updateWechatStatus(Long id, LeadWechatStatusUpdateRequest request) {
        Lead lead = getLeadOrThrow(id);
        lead.setWechatAddedStatus(request.getWechatAddedStatus());
        if (request.getFollowUpNote() != null) {
            lead.setFollowUpNote(request.getFollowUpNote());
        }
        lead.setLastFollowUpTime(LocalDateTime.now());
        leadMapper.updateById(lead);
    }

    @Override
    public void updateDealStatus(Long id, LeadDealStatusUpdateRequest request) {
        Lead lead = getLeadOrThrow(id);
        lead.setDealStatus(request.getDealStatus());
        if (request.getFollowUpNote() != null) {
            lead.setFollowUpNote(request.getFollowUpNote());
        }
        lead.setLastFollowUpTime(LocalDateTime.now());
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

    private void increaseConsultCount(Long resourceId) {
        if (resourceId == null) {
            return;
        }
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return;
        }
        int currentConsultCount = resource.getConsultCount() == null ? 0 : resource.getConsultCount();
        resource.setConsultCount(currentConsultCount + 1);
        resourceMapper.updateById(resource);
    }
}
