package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teacherresourcehub.common.constant.ConfigGroupConstants;
import com.teacherresourcehub.common.util.JsonUtils;
import com.teacherresourcehub.entity.SiteConfig;
import com.teacherresourcehub.mapper.SiteConfigMapper;
import com.teacherresourcehub.service.SiteConfigService;
import com.teacherresourcehub.vo.HomeConfigVO;
import com.teacherresourcehub.vo.SiteConfigVO;
import com.teacherresourcehub.vo.WechatConsultVO;
import com.teacherresourcehub.dto.HomeConfigSaveRequest;
import com.teacherresourcehub.dto.SiteConfigSaveRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    public SiteConfigServiceImpl(SiteConfigMapper siteConfigMapper) {
        this.siteConfigMapper = siteConfigMapper;
    }

    @Override
    public SiteConfigVO getSiteConfig() {
        Map<String, String> configMap = getConfigMapByGroup(ConfigGroupConstants.SITE);
        SiteConfigVO vo = new SiteConfigVO();
        vo.setSiteName(configMap.getOrDefault("site_name", ""));
        vo.setSeoTitle(configMap.getOrDefault("seo_title", ""));
        vo.setSeoKeywords(configMap.getOrDefault("seo_keywords", ""));
        vo.setSeoDescription(configMap.getOrDefault("seo_description", ""));
        vo.setContactPhone(configMap.getOrDefault("contact_phone", ""));
        vo.setContactWechat(configMap.getOrDefault("contact_wechat", ""));
        vo.setContactEmail(configMap.getOrDefault("contact_email", ""));
        vo.setFooterText(configMap.getOrDefault("footer_text", ""));
        return vo;
    }

    @Override
    public void saveSiteConfig(SiteConfigSaveRequest request) {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("site_name", request.getSiteName());
        configMap.put("seo_title", request.getSeoTitle());
        configMap.put("seo_keywords", request.getSeoKeywords());
        configMap.put("seo_description", request.getSeoDescription());
        configMap.put("contact_phone", valueOrEmpty(request.getContactPhone()));
        configMap.put("contact_wechat", valueOrEmpty(request.getContactWechat()));
        configMap.put("contact_email", valueOrEmpty(request.getContactEmail()));
        configMap.put("footer_text", valueOrEmpty(request.getFooterText()));
        saveGroupConfigs(ConfigGroupConstants.SITE, configMap);
    }

    @Override
    public HomeConfigVO getHomeConfig() {
        Map<String, String> configMap = getConfigMapByGroup(ConfigGroupConstants.HOME);
        HomeConfigVO vo = new HomeConfigVO();
        vo.setHomeMainTitle(configMap.getOrDefault("home_main_title", ""));
        vo.setHomeSubTitle(configMap.getOrDefault("home_sub_title", ""));
        vo.setWechatId(configMap.getOrDefault("wechat_id", ""));
        vo.setWechatQrUrl(configMap.getOrDefault("wechat_qr_url", ""));
        vo.setWechatTip(configMap.getOrDefault("wechat_tip", ""));
        vo.setHomeHotCategoryIds(parseLongList(configMap.get("home_hot_category_ids")));
        vo.setHomeRecommendedResourceIds(parseLongList(configMap.get("home_recommended_resource_ids")));
        vo.setHomeFaqIds(parseLongList(configMap.get("home_faq_ids")));
        vo.setQuickConsultBarText(configMap.getOrDefault("quick_consult_bar_text", ""));
        vo.setConsultNotice(configMap.getOrDefault("consult_notice", ""));
        vo.setDeliveryProcess(parseStringList(configMap.get("delivery_process_json")));
        return vo;
    }

    @Override
    public void saveHomeConfig(HomeConfigSaveRequest request) {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("home_main_title", request.getHomeMainTitle());
        configMap.put("home_sub_title", request.getHomeSubTitle());
        configMap.put("wechat_id", request.getWechatId());
        configMap.put("wechat_qr_url", request.getWechatQrUrl());
        configMap.put("wechat_tip", request.getWechatTip());
        configMap.put("home_hot_category_ids", JsonUtils.toJson(request.getHomeHotCategoryIds()));
        configMap.put("home_recommended_resource_ids", JsonUtils.toJson(request.getHomeRecommendedResourceIds()));
        configMap.put("home_faq_ids", JsonUtils.toJson(request.getHomeFaqIds()));
        configMap.put("quick_consult_bar_text", valueOrEmpty(request.getQuickConsultBarText()));
        configMap.put("consult_notice", valueOrEmpty(request.getConsultNotice()));
        configMap.put("delivery_process_json", JsonUtils.toJson(request.getDeliveryProcess()));
        saveGroupConfigs(ConfigGroupConstants.HOME, configMap);
    }

    @Override
    public WechatConsultVO getWechatConsult() {
        Map<String, String> configMap = getConfigMapByGroup(ConfigGroupConstants.HOME);
        WechatConsultVO vo = new WechatConsultVO();
        vo.setWechatId(configMap.getOrDefault("wechat_id", ""));
        vo.setWechatQrUrl(configMap.getOrDefault("wechat_qr_url", ""));
        vo.setWechatTip(configMap.getOrDefault("wechat_tip", ""));
        return vo;
    }

    private Map<String, String> getConfigMapByGroup(String group) {
        List<SiteConfig> list = siteConfigMapper.selectList(new LambdaQueryWrapper<SiteConfig>()
                .eq(SiteConfig::getConfigGroup, group));
        return list.stream().collect(Collectors.toMap(
                SiteConfig::getConfigKey,
                SiteConfig::getConfigValue,
                (a, b) -> b
        ));
    }

    private void saveGroupConfigs(String group, Map<String, String> values) {
        Map<String, SiteConfig> currentMap = siteConfigMapper.selectList(new LambdaQueryWrapper<SiteConfig>()
                        .eq(SiteConfig::getConfigGroup, group))
                .stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, Function.identity(), (a, b) -> b));

        values.forEach((key, value) -> {
            SiteConfig siteConfig = currentMap.get(key);
            if (siteConfig == null) {
                siteConfig = new SiteConfig();
                siteConfig.setConfigGroup(group);
                siteConfig.setConfigKey(key);
                siteConfig.setDescription("");
                siteConfig.setConfigValue(value);
                siteConfigMapper.insert(siteConfig);
            } else {
                siteConfig.setConfigValue(value);
                siteConfigMapper.updateById(siteConfig);
            }
        });
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private List<Long> parseLongList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        return JsonUtils.parse(json, new TypeReference<List<Long>>() {
        });
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        return JsonUtils.parse(json, new TypeReference<List<String>>() {
        });
    }
}
