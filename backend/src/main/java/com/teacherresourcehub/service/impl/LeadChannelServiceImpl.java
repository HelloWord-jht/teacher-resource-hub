package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.LeadChannelSaveRequest;
import com.teacherresourcehub.entity.LeadChannel;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.LeadChannelMapper;
import com.teacherresourcehub.service.LeadChannelService;
import com.teacherresourcehub.vo.LeadChannelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadChannelServiceImpl implements LeadChannelService {

    private final LeadChannelMapper leadChannelMapper;

    public LeadChannelServiceImpl(LeadChannelMapper leadChannelMapper) {
        this.leadChannelMapper = leadChannelMapper;
    }

    @Override
    public List<LeadChannelVO> list() {
        return leadChannelMapper.selectList(new LambdaQueryWrapper<LeadChannel>()
                        .orderByDesc(LeadChannel::getSortOrder, LeadChannel::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public LeadChannelVO getById(Long id) {
        return toVO(getOrThrow(id));
    }

    @Override
    public void create(LeadChannelSaveRequest request) {
        validateUnique(null, request.getChannelKey(), request.getChannelName());
        LeadChannel channel = new LeadChannel();
        BeanUtils.copyProperties(request, channel);
        leadChannelMapper.insert(channel);
    }

    @Override
    public void update(Long id, LeadChannelSaveRequest request) {
        LeadChannel channel = getOrThrow(id);
        validateUnique(id, request.getChannelKey(), request.getChannelName());
        BeanUtils.copyProperties(request, channel);
        leadChannelMapper.updateById(channel);
    }

    @Override
    public void delete(Long id) {
        getOrThrow(id);
        leadChannelMapper.deleteById(id);
    }

    private void validateUnique(Long id, String channelKey, String channelName) {
        LeadChannel byKey = leadChannelMapper.selectOne(new LambdaQueryWrapper<LeadChannel>()
                .eq(LeadChannel::getChannelKey, channelKey)
                .last("LIMIT 1"));
        if (byKey != null && !byKey.getId().equals(id)) {
            throw new BusinessException("渠道标识已存在");
        }

        LeadChannel byName = leadChannelMapper.selectOne(new LambdaQueryWrapper<LeadChannel>()
                .eq(LeadChannel::getChannelName, channelName)
                .last("LIMIT 1"));
        if (byName != null && !byName.getId().equals(id)) {
            throw new BusinessException("渠道名称已存在");
        }
    }

    private LeadChannel getOrThrow(Long id) {
        LeadChannel channel = leadChannelMapper.selectById(id);
        if (channel == null) {
            throw new BusinessException("渠道不存在");
        }
        return channel;
    }

    private LeadChannelVO toVO(LeadChannel channel) {
        LeadChannelVO vo = new LeadChannelVO();
        BeanUtils.copyProperties(channel, vo);
        return vo;
    }
}
