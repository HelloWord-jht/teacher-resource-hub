package com.teacherresourcehub.vo;

import java.util.List;

public class AdminResourceFileDetailVO extends AdminResourceFileItemVO {

    private String storagePath;
    private List<AdminResourceFilePreviewVO> previews;

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public List<AdminResourceFilePreviewVO> getPreviews() { return previews; }
    public void setPreviews(List<AdminResourceFilePreviewVO> previews) { this.previews = previews; }
}
