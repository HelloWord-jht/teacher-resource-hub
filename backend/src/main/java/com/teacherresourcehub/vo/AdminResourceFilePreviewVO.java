package com.teacherresourcehub.vo;

public class AdminResourceFilePreviewVO {

    private Integer pageNo;
    private String previewType;
    private String previewImageUrl;
    private String previewTextExcerpt;
    private Integer width;
    private Integer height;

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public String getPreviewType() { return previewType; }
    public void setPreviewType(String previewType) { this.previewType = previewType; }
    public String getPreviewImageUrl() { return previewImageUrl; }
    public void setPreviewImageUrl(String previewImageUrl) { this.previewImageUrl = previewImageUrl; }
    public String getPreviewTextExcerpt() { return previewTextExcerpt; }
    public void setPreviewTextExcerpt(String previewTextExcerpt) { this.previewTextExcerpt = previewTextExcerpt; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}
