package com.teacherresourcehub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("resource_file_preview")
public class ResourceFilePreview {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceFileId;
    private Integer pageNo;
    private String previewType;
    private String previewImageUrl;
    private String previewTextExcerpt;
    private Integer width;
    private Integer height;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getResourceFileId() { return resourceFileId; }
    public void setResourceFileId(Long resourceFileId) { this.resourceFileId = resourceFileId; }
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
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
