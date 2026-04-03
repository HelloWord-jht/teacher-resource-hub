package com.teacherresourcehub.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ResourceDetailVO {

    private Long id;
    private String title;
    private String slug;
    private String resourceCode;
    private Long categoryId;
    private String categoryName;
    private String summary;
    private String coverImage;
    private BigDecimal displayPrice;
    private String grade;
    private String scene;
    private Integer previewCount;
    private Integer contentItemCount;
    private List<String> previewImages;
    private List<TagVO> tags;
    private LocalDateTime publishTime;
    private LocalDateTime updatedAt;
    private List<String> contentItems;
    private String descriptionHtml;
    private String usageNotice;
    private String deliveryNotice;
    private List<FaqVO> faqList;
    private WechatConsultVO wechatConsult;
    private List<ResourceRelationVO> relatedResources;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(BigDecimal displayPrice) {
        this.displayPrice = displayPrice;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public Integer getPreviewCount() {
        return previewCount;
    }

    public void setPreviewCount(Integer previewCount) {
        this.previewCount = previewCount;
    }

    public Integer getContentItemCount() {
        return contentItemCount;
    }

    public void setContentItemCount(Integer contentItemCount) {
        this.contentItemCount = contentItemCount;
    }

    public List<String> getPreviewImages() {
        return previewImages;
    }

    public void setPreviewImages(List<String> previewImages) {
        this.previewImages = previewImages;
    }

    public List<TagVO> getTags() {
        return tags;
    }

    public void setTags(List<TagVO> tags) {
        this.tags = tags;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getContentItems() {
        return contentItems;
    }

    public void setContentItems(List<String> contentItems) {
        this.contentItems = contentItems;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getUsageNotice() {
        return usageNotice;
    }

    public void setUsageNotice(String usageNotice) {
        this.usageNotice = usageNotice;
    }

    public String getDeliveryNotice() {
        return deliveryNotice;
    }

    public void setDeliveryNotice(String deliveryNotice) {
        this.deliveryNotice = deliveryNotice;
    }

    public List<FaqVO> getFaqList() {
        return faqList;
    }

    public void setFaqList(List<FaqVO> faqList) {
        this.faqList = faqList;
    }

    public WechatConsultVO getWechatConsult() {
        return wechatConsult;
    }

    public void setWechatConsult(WechatConsultVO wechatConsult) {
        this.wechatConsult = wechatConsult;
    }

    public List<ResourceRelationVO> getRelatedResources() {
        return relatedResources;
    }

    public void setRelatedResources(List<ResourceRelationVO> relatedResources) {
        this.relatedResources = relatedResources;
    }
}
