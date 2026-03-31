package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.HomeConfigSaveRequest;
import com.teacherresourcehub.dto.SiteConfigSaveRequest;
import com.teacherresourcehub.vo.HomeConfigVO;
import com.teacherresourcehub.vo.SiteConfigVO;
import com.teacherresourcehub.vo.WechatConsultVO;

public interface SiteConfigService {

    SiteConfigVO getSiteConfig();

    void saveSiteConfig(SiteConfigSaveRequest request);

    HomeConfigVO getHomeConfig();

    void saveHomeConfig(HomeConfigSaveRequest request);

    WechatConsultVO getWechatConsult();
}
