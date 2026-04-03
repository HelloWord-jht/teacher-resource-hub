package com.teacherresourcehub.vo;

public class WebResourcePreviewPageVO {

    private Integer pageNo;
    private String previewType;
    private String previewImageUrl;
    private String previewTextExcerpt;

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public String getPreviewType() { return previewType; }
    public void setPreviewType(String previewType) { this.previewType = previewType; }
    public String getPreviewImageUrl() { return previewImageUrl; }
    public void setPreviewImageUrl(String previewImageUrl) { this.previewImageUrl = previewImageUrl; }
    public String getPreviewTextExcerpt() { return previewTextExcerpt; }
    public void setPreviewTextExcerpt(String previewTextExcerpt) { this.previewTextExcerpt = previewTextExcerpt; }
}
