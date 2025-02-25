package com.office.controller;

import com.office.dto.request.LeaveRequest;
import com.office.dto.request.LeaveApprovalRequest;
import com.office.service.LeaveService;
import com.office.exception.BusinessException;
import com.office.dto.response.ErrorResponse;
import com.office.entity.Leave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    @PostMapping
    public ResponseEntity<?> createLeave(
            @RequestBody LeaveRequest request,
            @RequestAttribute Integer currentUserId) {
        try {
            return ResponseEntity.ok(leaveService.createLeave(request, currentUserId));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{leaveId}/approve")
    public ResponseEntity<?> approveLeave(
            @PathVariable Integer leaveId,
            @RequestBody LeaveApprovalRequest request,
            @RequestAttribute Integer currentUserId) {
        try {
            leaveService.approveLeave(leaveId, request, currentUserId);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyLeaves(@RequestAttribute Integer currentUserId) {
        return ResponseEntity.ok(leaveService.getMyLeaves(currentUserId));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApprovals(@RequestAttribute Integer currentUserId) {
        return ResponseEntity.ok(leaveService.getPendingApprovals(currentUserId));
    }
    
    @GetMapping("/department/{deptId}")
    public ResponseEntity<?> getDepartmentLeaves(@PathVariable Integer deptId) {
        return ResponseEntity.ok(leaveService.getDepartmentLeaves(deptId));
    }
    
    @GetMapping("/{leaveId}")
    public ResponseEntity<?> getLeaveById(@PathVariable Integer leaveId) {
        try {
            System.out.println("获取请假详情请求, ID: " + leaveId);
            Leave leave = leaveService.getLeaveById(leaveId);
            System.out.println("返回请假详情: " + leave);
            return ResponseEntity.ok(leave);
        } catch (BusinessException e) {
            System.err.println("获取请假详情失败: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
} 