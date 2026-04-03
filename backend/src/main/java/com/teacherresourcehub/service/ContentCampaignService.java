package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.ContentCampaignSaveRequest;
import com.teacherresourcehub.vo.ContentCampaignVO;

import java.util.List;

public interface ContentCampaignService {

    List<ContentCampaignVO> list(Long channelId);

    ContentCampaignVO getById(Long id);

    void create(ContentCampaignSaveRequest request);

    void update(Long id, ContentCampaignSaveRequest request);

    void delete(Long id);
}
