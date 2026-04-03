package com.teacherresourcehub.vo;

public class WebResourcePreviewFileVO {

    private Long id;
    private String fileName;
    private String fileType;
    private Integer previewPageCount;
    private Boolean primary;
    private Boolean current;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Integer getPreviewPageCount() { return previewPageCount; }
    public void setPreviewPageCount(Integer previewPageCount) { this.previewPageCount = previewPageCount; }
    public Boolean getPrimary() { return primary; }
    public void setPrimary(Boolean primary) { this.primary = primary; }
    public Boolean getCurrent() { return current; }
    public void setCurrent(Boolean current) { this.current = current; }
}
