package com.teacherresourcehub.vo;

import java.util.List;

public class DashboardVO {

    private Long resourceTotal;
    private Long publishedResourceTotal;
    private Long pendingResourceTotal;
    private Long riskResourceTotal;
    private Long categoryTotal;
    private Long tagTotal;
    private Long leadTotal;
    private Long todayLeadTotal;
    private Long todayWechatAddedTotal;
    private Long todayDealTotal;
    private Long todayDeliveryTotal;
    private List<DashboardRecentResourceVO> recentResources;
    private List<DashboardRecentLeadVO> recentLeads;
    private List<DeliveryRecordVO> recentDeliveries;
    private List<ImportTaskVO> recentImportTasks;
    private List<DashboardChannelStatVO> channelLeadStats;
    private List<DashboardResourceRankVO> hotViewResources;
    private List<DashboardResourceRankVO> hotConsultResources;

    public Long getResourceTotal() { return resourceTotal; }
    public void setResourceTotal(Long resourceTotal) { this.resourceTotal = resourceTotal; }
    public Long getPublishedResourceTotal() { return publishedResourceTotal; }
    public void setPublishedResourceTotal(Long publishedResourceTotal) { this.publishedResourceTotal = publishedResourceTotal; }
    public Long getPendingResourceTotal() { return pendingResourceTotal; }
    public void setPendingResourceTotal(Long pendingResourceTotal) { this.pendingResourceTotal = pendingResourceTotal; }
    public Long getRiskResourceTotal() { return riskResourceTotal; }
    public void setRiskResourceTotal(Long riskResourceTotal) { this.riskResourceTotal = riskResourceTotal; }
    public Long getCategoryTotal() { return categoryTotal; }
    public void setCategoryTotal(Long categoryTotal) { this.categoryTotal = categoryTotal; }
    public Long getTagTotal() { return tagTotal; }
    public void setTagTotal(Long tagTotal) { this.tagTotal = tagTotal; }
    public Long getLeadTotal() { return leadTotal; }
    public void setLeadTotal(Long leadTotal) { this.leadTotal = leadTotal; }
    public Long getTodayLeadTotal() { return todayLeadTotal; }
    public void setTodayLeadTotal(Long todayLeadTotal) { this.todayLeadTotal = todayLeadTotal; }
    public Long getTodayWechatAddedTotal() { return todayWechatAddedTotal; }
    public void setTodayWechatAddedTotal(Long todayWechatAddedTotal) { this.todayWechatAddedTotal = todayWechatAddedTotal; }
    public Long getTodayDealTotal() { return todayDealTotal; }
    public void setTodayDealTotal(Long todayDealTotal) { this.todayDealTotal = todayDealTotal; }
    public Long getTodayDeliveryTotal() { return todayDeliveryTotal; }
    public void setTodayDeliveryTotal(Long todayDeliveryTotal) { this.todayDeliveryTotal = todayDeliveryTotal; }
    public List<DashboardRecentResourceVO> getRecentResources() { return recentResources; }
    public void setRecentResources(List<DashboardRecentResourceVO> recentResources) { this.recentResources = recentResources; }
    public List<DashboardRecentLeadVO> getRecentLeads() { return recentLeads; }
    public void setRecentLeads(List<DashboardRecentLeadVO> recentLeads) { this.recentLeads = recentLeads; }
    public List<DeliveryRecordVO> getRecentDeliveries() { return recentDeliveries; }
    public void setRecentDeliveries(List<DeliveryRecordVO> recentDeliveries) { this.recentDeliveries = recentDeliveries; }
    public List<ImportTaskVO> getRecentImportTasks() { return recentImportTasks; }
    public void setRecentImportTasks(List<ImportTaskVO> recentImportTasks) { this.recentImportTasks = recentImportTasks; }
    public List<DashboardChannelStatVO> getChannelLeadStats() { return channelLeadStats; }
    public void setChannelLeadStats(List<DashboardChannelStatVO> channelLeadStats) { this.channelLeadStats = channelLeadStats; }
    public List<DashboardResourceRankVO> getHotViewResources() { return hotViewResources; }
    public void setHotViewResources(List<DashboardResourceRankVO> hotViewResources) { this.hotViewResources = hotViewResources; }
    public List<DashboardResourceRankVO> getHotConsultResources() { return hotConsultResources; }
    public void setHotConsultResources(List<DashboardResourceRankVO> hotConsultResources) { this.hotConsultResources = hotConsultResources; }
}
