package com.office.service;

import com.office.entity.Leave;
import com.office.dto.request.LeaveRequest;
import com.office.dto.request.LeaveApprovalRequest;
import java.util.List;

public interface LeaveService {
    Leave createLeave(LeaveRequest request, Integer applicantId);
    void approveLeave(Integer leaveId, LeaveApprovalRequest request, Integer approverId);
    List<Leave> getMyLeaves(Integer userId);
    List<Leave> getPendingApprovals(Integer approverId);
    List<Leave> getDepartmentLeaves(Integer deptId);
    Leave getLeaveById(Integer leaveId);
} 