package com.teacherresourcehub.vo;

import java.util.List;

public class HomePageVO {

    private String siteName;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private String homeMainTitle;
    private String homeSubTitle;
    private String footerText;
    private String quickConsultBarText;
    private String consultNotice;
    private List<String> deliveryProcess;
    private List<CategoryVO> hotCategories;
    private List<ResourceListItemVO> recommendedResources;
    private List<FaqVO> faqList;
    private WechatConsultVO wechatConsult;

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public String getHomeMainTitle() { return homeMainTitle; }
    public void setHomeMainTitle(String homeMainTitle) { this.homeMainTitle = homeMainTitle; }
    public String getHomeSubTitle() { return homeSubTitle; }
    public void setHomeSubTitle(String homeSubTitle) { this.homeSubTitle = homeSubTitle; }
    public String getFooterText() { return footerText; }
    public void setFooterText(String footerText) { this.footerText = footerText; }
    public String getQuickConsultBarText() { return quickConsultBarText; }
    public void setQuickConsultBarText(String quickConsultBarText) { this.quickConsultBarText = quickConsultBarText; }
    public String getConsultNotice() { return consultNotice; }
    public void setConsultNotice(String consultNotice) { this.consultNotice = consultNotice; }
    public List<String> getDeliveryProcess() { return deliveryProcess; }
    public void setDeliveryProcess(List<String> deliveryProcess) { this.deliveryProcess = deliveryProcess; }
    public List<CategoryVO> getHotCategories() { return hotCategories; }
    public void setHotCategories(List<CategoryVO> hotCategories) { this.hotCategories = hotCategories; }
    public List<ResourceListItemVO> getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(List<ResourceListItemVO> recommendedResources) { this.recommendedResources = recommendedResources; }
    public List<FaqVO> getFaqList() { return faqList; }
    public void setFaqList(List<FaqVO> faqList) { this.faqList = faqList; }
    public WechatConsultVO getWechatConsult() { return wechatConsult; }
    public void setWechatConsult(WechatConsultVO wechatConsult) { this.wechatConsult = wechatConsult; }
}
