package com.teacherresourcehub.service.impl;

import com.teacherresourcehub.service.CategoryService;
import com.teacherresourcehub.service.FaqService;
import com.teacherresourcehub.service.HomeService;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.service.SiteConfigService;
import com.teacherresourcehub.vo.HomeConfigVO;
import com.teacherresourcehub.vo.HomePageVO;
import com.teacherresourcehub.vo.SiteConfigVO;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    private final SiteConfigService siteConfigService;
    private final CategoryService categoryService;
    private final ResourceService resourceService;
    private final FaqService faqService;

    public HomeServiceImpl(SiteConfigService siteConfigService,
                           CategoryService categoryService,
                           ResourceService resourceService,
                           FaqService faqService) {
        this.siteConfigService = siteConfigService;
        this.categoryService = categoryService;
        this.resourceService = resourceService;
        this.faqService = faqService;
    }

    @Override
    public HomePageVO getHomePage() {
        SiteConfigVO siteConfig = siteConfigService.getSiteConfig();
        HomeConfigVO homeConfig = siteConfigService.getHomeConfig();

        HomePageVO vo = new HomePageVO();
        vo.setSiteName(siteConfig.getSiteName());
        vo.setSeoTitle(siteConfig.getSeoTitle());
        vo.setSeoKeywords(siteConfig.getSeoKeywords());
        vo.setSeoDescription(siteConfig.getSeoDescription());
        vo.setFooterText(siteConfig.getFooterText());
        vo.setHomeMainTitle(homeConfig.getHomeMainTitle());
        vo.setHomeSubTitle(homeConfig.getHomeSubTitle());
        vo.setHotCategories(categoryService.listByIds(homeConfig.getHomeHotCategoryIds()));
        vo.setRecommendedResources(resourceService.listPublishedResourceListItemsByIds(homeConfig.getHomeRecommendedResourceIds()));
        vo.setFaqList(homeConfig.getHomeFaqIds() == null || homeConfig.getHomeFaqIds().isEmpty()
                ? faqService.listEnabledFaqs().stream().limit(6).toList()
                : faqService.listByIds(homeConfig.getHomeFaqIds()));
        vo.setWechatConsult(siteConfigService.getWechatConsult());
        return vo;
    }
}
