package com.teacherresourcehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class HomeConfigSaveRequest {

    @NotBlank(message = "首页主标题不能为空")
    private String homeMainTitle;
    @NotBlank(message = "首页副标题不能为空")
    private String homeSubTitle;
    @NotBlank(message = "微信号不能为空")
    private String wechatId;
    @NotBlank(message = "微信二维码地址不能为空")
    private String wechatQrUrl;
    @NotBlank(message = "微信引导文案不能为空")
    private String wechatTip;
    @NotEmpty(message = "热门分类不能为空")
    private List<Long> homeHotCategoryIds;
    @NotEmpty(message = "推荐资源不能为空")
    private List<Long> homeRecommendedResourceIds;
    @NotEmpty(message = "首页FAQ不能为空")
    private List<Long> homeFaqIds;
    @NotBlank(message = "快速咨询提示条不能为空")
    private String quickConsultBarText;
    @NotBlank(message = "咨询提示不能为空")
    private String consultNotice;
    @NotEmpty(message = "交付流程不能为空")
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
