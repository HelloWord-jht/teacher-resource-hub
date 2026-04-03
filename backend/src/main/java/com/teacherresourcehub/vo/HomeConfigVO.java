package com.teacherresourcehub.vo;

import java.util.List;

public class HomeConfigVO {

    private String homeMainTitle;
    private String homeSubTitle;
    private String wechatId;
    private String wechatQrUrl;
    private String wechatTip;
    private List<Long> homeHotCategoryIds;
    private List<Long> homeRecommendedResourceIds;
    private List<Long> homeFaqIds;
    private String quickConsultBarText;
    private String consultNotice;
    private List<String> deliveryProcess;

    public String getHomeMainTitle() { return homeMainTitle; }
    public void setHomeMainTitle(String homeMainTitle) { this.homeMainTitle = homeMainTitle; }
    public String getHomeSubTitle() { return homeSubTitle; }
    public void setHomeSubTitle(String homeSubTitle) { this.homeSubTitle = homeSubTitle; }
    public String getWechatId() { return wechatId; }
    public void setWechatId(String wechatId) { this.wechatId = wechatId; }
    public String getWechatQrUrl() { return wechatQrUrl; }
    public void setWechatQrUrl(String wechatQrUrl) { this.wechatQrUrl = wechatQrUrl; }
    public String getWechatTip() { return wechatTip; }
    public void setWechatTip(String wechatTip) { this.wechatTip = wechatTip; }
    public List<Long> getHomeHotCategoryIds() { return homeHotCategoryIds; }
    public void setHomeHotCategoryIds(List<Long> homeHotCategoryIds) { this.homeHotCategoryIds = homeHotCategoryIds; }
    public List<Long> getHomeRecommendedResourceIds() { return homeRecommendedResourceIds; }
    public void setHomeRecommendedResourceIds(List<Long> homeRecommendedResourceIds) { this.homeRecommendedResourceIds = homeRecommendedResourceIds; }
    public List<Long> getHomeFaqIds() { return homeFaqIds; }
    public void setHomeFaqIds(List<Long> homeFaqIds) { this.homeFaqIds = homeFaqIds; }
    public String getQuickConsultBarText() { return quickConsultBarText; }
    public void setQuickConsultBarText(String quickConsultBarText) { this.quickConsultBarText = quickConsultBarText; }
    public String getConsultNotice() { return consultNotice; }
    public void setConsultNotice(String consultNotice) { this.consultNotice = consultNotice; }
    public List<String> getDeliveryProcess() { return deliveryProcess; }
    public void setDeliveryProcess(List<String> deliveryProcess) { this.deliveryProcess = deliveryProcess; }
}
