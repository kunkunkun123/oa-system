package com.office.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "leaves")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "applicant_id", nullable = false)
    private Integer applicantId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"password", "userRole", "department"})
    private User applicant;
    
    @Column(name = "type", nullable = false)
    private Integer type;  // 1: 事假, 2: 病假, 3: 年假, 4: 调休
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "reason", nullable = false)
    private String reason;
    
    @Column(name = "status", nullable = false)
    private Integer status;  // 0: 待审批, 1: 审批中, 2: 已通过, 3: 已拒绝
    
    @Column(name = "current_approver_id")
    private Integer currentApproverId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_approver_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"password", "userRole", "department"})
    private User currentApprover;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCurrentApproverId() {
        return currentApproverId;
    }

    public void setCurrentApproverId(Integer currentApproverId) {
        this.currentApproverId = currentApproverId;
    }

    public User getCurrentApprover() {
        return currentApprover;
    }

    public void setCurrentApprover(User currentApprover) {
        this.currentApprover = currentApprover;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
} 