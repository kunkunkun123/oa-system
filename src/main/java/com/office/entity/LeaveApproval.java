package com.office.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_approvals")
public class LeaveApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "leave_id", nullable = false)
    private Integer leaveId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id", insertable = false, updatable = false)
    private Leave leave;
    
    @Column(name = "approver_id", nullable = false)
    private Integer approverId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", insertable = false, updatable = false)
    private User approver;
    
    @Column(name = "status", nullable = false)
    private Integer status;  // 1: 通过, 2: 拒绝
    
    @Column(name = "comment")
    private String comment;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Leave getLeave() {
        return leave;
    }

    public void setLeave(Leave leave) {
        this.leave = leave;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
} 