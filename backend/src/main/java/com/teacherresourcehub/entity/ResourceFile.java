package com.teacherresourcehub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("resource_file")
public class ResourceFile {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceId;
    private Long importTaskId;
    private Long parentZipFileId;
    private String fileName;
    private String originalFileName;
    private String fileExt;
    private String detectedType;
    private String mimeType;
    private Long fileSize;
    private String storagePath;
    private String archiveEntryPath;
    private String sourceType;
    private Integer sortOrder;
    private Integer isPrimary;
    private String previewStatus;
    private Integer previewPageCount;
    private String previewErrorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public Long getImportTaskId() { return importTaskId; }
    public void setImportTaskId(Long importTaskId) { this.importTaskId = importTaskId; }
    public Long getParentZipFileId() { return parentZipFileId; }
    public void setParentZipFileId(Long parentZipFileId) { this.parentZipFileId = parentZipFileId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getFileExt() { return fileExt; }
    public void setFileExt(String fileExt) { this.fileExt = fileExt; }
    public String getDetectedType() { return detectedType; }
    public void setDetectedType(String detectedType) { this.detectedType = detectedType; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getArchiveEntryPath() { return archiveEntryPath; }
    public void setArchiveEntryPath(String archiveEntryPath) { this.archiveEntryPath = archiveEntryPath; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Integer isPrimary) { this.isPrimary = isPrimary; }
    public String getPreviewStatus() { return previewStatus; }
    public void setPreviewStatus(String previewStatus) { this.previewStatus = previewStatus; }
    public Integer getPreviewPageCount() { return previewPageCount; }
    public void setPreviewPageCount(Integer previewPageCount) { this.previewPageCount = previewPageCount; }
    public String getPreviewErrorMessage() { return previewErrorMessage; }
    public void setPreviewErrorMessage(String previewErrorMessage) { this.previewErrorMessage = previewErrorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
