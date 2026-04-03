package com.teacherresourcehub.service;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.LeadDealStatusUpdateRequest;
import com.teacherresourcehub.dto.LeadCreateRequest;
import com.teacherresourcehub.dto.LeadStatusUpdateRequest;
import com.teacherresourcehub.dto.LeadWechatStatusUpdateRequest;
import com.teacherresourcehub.vo.DashboardRecentLeadVO;
import com.teacherresourcehub.vo.LeadVO;

import java.util.List;

public interface LeadService {

    void createLead(LeadCreateRequest request);

    PageResult<LeadVO> pageAdminLeads(Integer status,
                                      String channel,
                                      Integer wechatAddedStatus,
                                      Integer dealStatus,
                                      String keyword,
                                      String targetResourceCode,
                                      Long pageNum,
                                      Long pageSize);

    LeadVO getLeadDetail(Long id);

    void updateLeadStatus(Long id, LeadStatusUpdateRequest request);

    void updateWechatStatus(Long id, LeadWechatStatusUpdateRequest request);

    void updateDealStatus(Long id, LeadDealStatusUpdateRequest request);

    List<DashboardRecentLeadVO> listRecentLeads(int limit);

    long countTotal();
}
