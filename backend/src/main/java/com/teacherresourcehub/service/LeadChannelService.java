package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.LeadChannelSaveRequest;
import com.teacherresourcehub.vo.LeadChannelVO;

import java.util.List;

public interface LeadChannelService {

    List<LeadChannelVO> list();

    LeadChannelVO getById(Long id);

    void create(LeadChannelSaveRequest request);

    void update(Long id, LeadChannelSaveRequest request);

    void delete(Long id);
}
