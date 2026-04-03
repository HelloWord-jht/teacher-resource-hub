package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.ContentCampaignSaveRequest;
import com.teacherresourcehub.entity.ContentCampaign;
import com.teacherresourcehub.entity.LeadChannel;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.ContentCampaignMapper;
import com.teacherresourcehub.mapper.LeadChannelMapper;
import com.teacherresourcehub.service.ContentCampaignService;
import com.teacherresourcehub.vo.ContentCampaignVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContentCampaignServiceImpl implements ContentCampaignService {

    private final ContentCampaignMapper contentCampaignMapper;
    private final LeadChannelMapper leadChannelMapper;

    public ContentCampaignServiceImpl(ContentCampaignMapper contentCampaignMapper, LeadChannelMapper leadChannelMapper) {
        this.contentCampaignMapper = contentCampaignMapper;
        this.leadChannelMapper = leadChannelMapper;
    }

    @Override
    public List<ContentCampaignVO> list(Long channelId) {
        LambdaQueryWrapper<ContentCampaign> wrapper = new LambdaQueryWrapper<ContentCampaign>()
                .orderByDesc(ContentCampaign::getPublishTime, ContentCampaign::getId);
        if (channelId != null) {
            wrapper.eq(ContentCampaign::getChannelId, channelId);
        }
        List<ContentCampaign> list = contentCampaignMapper.selectList(wrapper);
        Map<Long, String> channelNameMap = leadChannelMapper.selectList(new LambdaQueryWrapper<LeadChannel>())
                .stream()
                .collect(Collectors.toMap(LeadChannel::getId, LeadChannel::getChannelName, (a, b) -> a));
        return list.stream().map(item -> {
            ContentCampaignVO vo = new ContentCampaignVO();
            BeanUtils.copyProperties(item, vo);
            vo.setChannelName(channelNameMap.getOrDefault(item.getChannelId(), ""));
            return vo;
        }).toList();
    }

    @Override
    public ContentCampaignVO getById(Long id) {
        ContentCampaign campaign = getOrThrow(id);
        ContentCampaignVO vo = new ContentCampaignVO();
        BeanUtils.copyProperties(campaign, vo);
        LeadChannel channel = leadChannelMapper.selectById(campaign.getChannelId());
        vo.setChannelName(channel == null ? "" : channel.getChannelName());
        return vo;
    }

    @Override
    public void create(ContentCampaignSaveRequest request) {
        validateTrackingCode(null, request.getTrackingCode());
        ensureChannelExists(request.getChannelId());
        ContentCampaign campaign = new ContentCampaign();
        BeanUtils.copyProperties(request, campaign);
        campaign.setTargetResourceCode(request.getTargetResourceCode() == null ? "" : request.getTargetResourceCode().trim());
        campaign.setLandingPage(request.getLandingPage() == null ? "" : request.getLandingPage().trim());
        campaign.setRemark(request.getRemark() == null ? "" : request.getRemark().trim());
        contentCampaignMapper.insert(campaign);
    }

    @Override
    public void update(Long id, ContentCampaignSaveRequest request) {
        ContentCampaign campaign = getOrThrow(id);
        validateTrackingCode(id, request.getTrackingCode());
        ensureChannelExists(request.getChannelId());
        BeanUtils.copyProperties(request, campaign);
        campaign.setTargetResourceCode(request.getTargetResourceCode() == null ? "" : request.getTargetResourceCode().trim());
        campaign.setLandingPage(request.getLandingPage() == null ? "" : request.getLandingPage().trim());
        campaign.setRemark(request.getRemark() == null ? "" : request.getRemark().trim());
        contentCampaignMapper.updateById(campaign);
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        contentCampaignMapper.deleteById(id);
    }

    private void validateTrackingCode(Long id, String trackingCode) {
        ContentCampaign byTrackingCode = contentCampaignMapper.selectOne(new LambdaQueryWrapper<ContentCampaign>()
                .eq(ContentCampaign::getTrackingCode, trackingCode)
                .last("LIMIT 1"));
        if (byTrackingCode != null && !byTrackingCode.getId().equals(id)) {
            throw new BusinessException("追踪码已存在");
        }
    }

    private void ensureChannelExists(Long channelId) {
        if (leadChannelMapper.selectById(channelId) == null) {
            throw new BusinessException("渠道不存在");
        }
    }

    private ContentCampaign getOrThrow(Long id) {
        ContentCampaign campaign = contentCampaignMapper.selectById(id);
        if (campaign == null) {
            throw new BusinessException("投放记录不存在");
        }
        return campaign;
    }
}
