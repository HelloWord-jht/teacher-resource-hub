package com.teacherresourcehub.vo;

public class AdminResourceFileItemVO {

    private Long id;
    private Long resourceId;
    private Long importTaskId;
    private Long parentZipFileId;
    private String fileName;
    private String fileType;
    private String mimeType;
    private Long fileSize;
    private String sourceType;
    private String archiveEntryPath;
    private Integer isPrimary;
    private String previewStatus;
    private Integer previewPageCount;
    private String previewErrorMessage;

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
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getArchiveEntryPath() { return archiveEntryPath; }
    public void setArchiveEntryPath(String archiveEntryPath) { this.archiveEntryPath = archiveEntryPath; }
    public Integer getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Integer isPrimary) { this.isPrimary = isPrimary; }
    public String getPreviewStatus() { return previewStatus; }
    public void setPreviewStatus(String previewStatus) { this.previewStatus = previewStatus; }
    public Integer getPreviewPageCount() { return previewPageCount; }
    public void setPreviewPageCount(Integer previewPageCount) { this.previewPageCount = previewPageCount; }
    public String getPreviewErrorMessage() { return previewErrorMessage; }
    public void setPreviewErrorMessage(String previewErrorMessage) { this.previewErrorMessage = previewErrorMessage; }
}
