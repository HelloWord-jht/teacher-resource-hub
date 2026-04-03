package com.teacherresourcehub.vo;

import java.time.LocalDateTime;
import java.util.List;

public class ImportTaskVO {

    private Long id;
    private String taskName;
    private String importType;
    private String rawPayload;
    private String importStatus;
    private Long generatedResourceId;
    private String generatedResourceCode;
    private Integer totalFileCount;
    private Integer recognizedFileCount;
    private Integer processedFileCount;
    private Integer previewSuccessCount;
    private Integer previewFailedCount;
    private Integer unsupportedFileCount;
    private String unzipStatus;
    private String previewStatusSummary;
    private Long recommendedCategoryId;
    private List<Long> recommendedTagIds;
    private String executionResult;
    private String operatorName;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getImportType() { return importType; }
    public void setImportType(String importType) { this.importType = importType; }
    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }
    public String getImportStatus() { return importStatus; }
    public void setImportStatus(String importStatus) { this.importStatus = importStatus; }
    public Long getGeneratedResourceId() { return generatedResourceId; }
    public void setGeneratedResourceId(Long generatedResourceId) { this.generatedResourceId = generatedResourceId; }
    public String getGeneratedResourceCode() { return generatedResourceCode; }
    public void setGeneratedResourceCode(String generatedResourceCode) { this.generatedResourceCode = generatedResourceCode; }
    public Integer getTotalFileCount() { return totalFileCount; }
    public void setTotalFileCount(Integer totalFileCount) { this.totalFileCount = totalFileCount; }
    public Integer getRecognizedFileCount() { return recognizedFileCount; }
    public void setRecognizedFileCount(Integer recognizedFileCount) { this.recognizedFileCount = recognizedFileCount; }
    public Integer getProcessedFileCount() { return processedFileCount; }
    public void setProcessedFileCount(Integer processedFileCount) { this.processedFileCount = processedFileCount; }
    public Integer getPreviewSuccessCount() { return previewSuccessCount; }
    public void setPreviewSuccessCount(Integer previewSuccessCount) { this.previewSuccessCount = previewSuccessCount; }
    public Integer getPreviewFailedCount() { return previewFailedCount; }
    public void setPreviewFailedCount(Integer previewFailedCount) { this.previewFailedCount = previewFailedCount; }
    public Integer getUnsupportedFileCount() { return unsupportedFileCount; }
    public void setUnsupportedFileCount(Integer unsupportedFileCount) { this.unsupportedFileCount = unsupportedFileCount; }
    public String getUnzipStatus() { return unzipStatus; }
    public void setUnzipStatus(String unzipStatus) { this.unzipStatus = unzipStatus; }
    public String getPreviewStatusSummary() { return previewStatusSummary; }
    public void setPreviewStatusSummary(String previewStatusSummary) { this.previewStatusSummary = previewStatusSummary; }
    public Long getRecommendedCategoryId() { return recommendedCategoryId; }
    public void setRecommendedCategoryId(Long recommendedCategoryId) { this.recommendedCategoryId = recommendedCategoryId; }
    public List<Long> getRecommendedTagIds() { return recommendedTagIds; }
    public void setRecommendedTagIds(List<Long> recommendedTagIds) { this.recommendedTagIds = recommendedTagIds; }
    public String getExecutionResult() { return executionResult; }
    public void setExecutionResult(String executionResult) { this.executionResult = executionResult; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
