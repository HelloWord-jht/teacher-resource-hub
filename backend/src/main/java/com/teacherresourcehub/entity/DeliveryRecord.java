package com.teacherresourcehub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("delivery_record")
public class DeliveryRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceId;
    private String resourceCode;
    private Long leadId;
    private String deliveryChannel;
    private String deliveryContentSnapshot;
    private String deliveryRemark;
    private String operatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getResourceCode() { return resourceCode; }
    public void setResourceCode(String resourceCode) { this.resourceCode = resourceCode; }
    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }
    public String getDeliveryChannel() { return deliveryChannel; }
    public void setDeliveryChannel(String deliveryChannel) { this.deliveryChannel = deliveryChannel; }
    public String getDeliveryContentSnapshot() { return deliveryContentSnapshot; }
    public void setDeliveryContentSnapshot(String deliveryContentSnapshot) { this.deliveryContentSnapshot = deliveryContentSnapshot; }
    public String getDeliveryRemark() { return deliveryRemark; }
    public void setDeliveryRemark(String deliveryRemark) { this.deliveryRemark = deliveryRemark; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
