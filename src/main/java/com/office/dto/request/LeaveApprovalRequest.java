package com.office.dto.request;

import lombok.Data;

@Data
public class LeaveApprovalRequest {
    private Integer status;  // 1: 同意, 2: 拒绝
    private String comment;
} 