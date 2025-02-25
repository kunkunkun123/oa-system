package com.office.service.impl;

import com.office.entity.Leave;
import com.office.entity.LeaveApproval;
import com.office.entity.User;
import com.office.entity.Department;
import com.office.repository.LeaveRepository;
import com.office.repository.LeaveApprovalRepository;
import com.office.repository.UserRepository;
import com.office.repository.DepartmentRepository;
import com.office.service.LeaveService;
import com.office.dto.request.LeaveRequest;
import com.office.dto.request.LeaveApprovalRequest;
import com.office.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.office.service.MessageService;
import com.office.entity.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class LeaveServiceImpl implements LeaveService {
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private LeaveApprovalRepository leaveApprovalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    @Transactional
    public Leave createLeave(LeaveRequest request, Integer applicantId) {
        // 验证请假时间
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException("请假时间不能为空");
        }
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        // 获取申请人信息
        User applicant = userRepository.findById(applicantId)
            .orElseThrow(() -> new BusinessException("申请人不存在"));
            
        System.out.println("申请人信息:");
        System.out.println("- ID: " + applicant.getUserId());
        System.out.println("- 部门ID: " + applicant.getDeptId());
        System.out.println("- 角色ID: " + applicant.getRoleId());
        
        // 确定审批人
        Integer approverId;
        
        if (applicant.getDeptId() == null) {
            throw new BusinessException("您尚未分配部门，请联系管理员");
        }
        
        // 获取部门信息
        Department dept = departmentRepository.findById(applicant.getDeptId())
            .orElseThrow(() -> new BusinessException("部门不存在"));
        
        System.out.println("部门信息:");
        System.out.println("- ID: " + dept.getDeptId());
        System.out.println("- 经理ID: " + dept.getManagerId());
        
        // 判断申请人身份和设置审批人
        if (applicant.getRoleId() == 3) { // 如果申请人是部门经理
            // 由管理员审批
            User admin = userRepository.findByRoleId(1)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("系统中没有管理员用户，无法处理部门经理的请假申请"));
            approverId = admin.getUserId();
            System.out.println("部门经理请假，设置管理员审批");
        } else if (dept.getManagerId() != null) {
            // 普通员工由部门经理审批
            approverId = dept.getManagerId();
            System.out.println("普通员工请假，设置部门经理审批");
        } else {
            throw new BusinessException("部门未设置经理，无法处理请假申请");
        }
        
        System.out.println("设置审批人ID: " + approverId);
        
        // 创建请假记录
        Leave leave = new Leave();
        leave.setApplicantId(applicantId);
        leave.setType(request.getType());
        leave.setStartTime(request.getStartTime());
        leave.setEndTime(request.getEndTime());
        leave.setReason(request.getReason());
        leave.setStatus(0); // 待审批
        leave.setCreateTime(LocalDateTime.now());
        leave.setCurrentApproverId(approverId);
        
        Leave savedLeave = leaveRepository.save(leave);

        // 发送消息通知审批人
        Message message = new Message();
        message.setSenderId(applicantId); // 发送者为申请人
        message.setReceiverId(approverId); // 接收者为审批人
        message.setTitle("新的请假申请");
        message.setContent("请假申请详情: \n请假类型: " + request.getType() + "\n开始时间: " + request.getStartTime() + "\n结束时间: " + request.getEndTime() + "\n原因: " + request.getReason());
        messageService.sendMessage(message); // 调用消息服务发送消息

        return savedLeave;
    }
    
    @Override
    @Transactional
    public void approveLeave(Integer leaveId, LeaveApprovalRequest request, Integer approverId) {
        Leave leave = getLeaveById(leaveId);
        
        // 验证是否为当前审批人
        if (!approverId.equals(leave.getCurrentApproverId())) {
            throw new BusinessException("您不是当前审批人");
        }
        
        // 创建审批记录
        LeaveApproval approval = new LeaveApproval();
        approval.setLeaveId(leaveId);
        approval.setApproverId(approverId);
        approval.setStatus(request.getStatus());
        approval.setComment(request.getComment());
        approval.setCreateTime(LocalDateTime.now());
        
        // 保存审批记录
        leaveApprovalRepository.save(approval);
        
        // 更新请假单状态
        if (request.getStatus() == 2) { // 拒绝
            leave.setStatus(3); // 已拒绝
            leave.setCurrentApproverId(null); // 清除审批人，因为已经审批完成
        } else if (request.getStatus() == 1) { // 同意
            leave.setStatus(2); // 已通过
            leave.setCurrentApproverId(null); // 清除审批人，因为已经审批完成
        }
        
        leave.setUpdateTime(LocalDateTime.now());
        leaveRepository.save(leave);
    }
    
    @Override
    public List<Leave> getMyLeaves(Integer userId) {
        List<Leave> leaves = leaveRepository.findByApplicantIdOrderByCreateTimeDesc(userId);
        // 加载关联数据
        for (Leave leave : leaves) {
            if (leave.getApplicantId() != null) {
                leave.setApplicant(userRepository.findById(leave.getApplicantId()).orElse(null));
            }
            if (leave.getCurrentApproverId() != null) {
                leave.setCurrentApprover(userRepository.findById(leave.getCurrentApproverId()).orElse(null));
            }
        }
        return leaves;
    }
    
    @Override
    public List<Leave> getPendingApprovals(Integer approverId) {
        System.out.println("获取待审批请假, 审批人ID: " + approverId);
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new BusinessException("审批人不存在"));
        
        List<Leave> leaves;
        if (approver.getRoleId() == 1) {  // 管理员
            System.out.println("管理员查看所有待审批请假");
            leaves = leaveRepository.findByStatus(0);  // 获取所有待审批的请假
        } else if (approver.getRoleId() == 3) {  // 部门经理
            System.out.println("部门经理查看本部门待审批请假");
            leaves = leaveRepository.findByCurrentApproverIdAndStatus(approverId, 0);
        } else {
            System.out.println("普通用户无审批权限");
            leaves = new ArrayList<>();
        }
        
        System.out.println("查询到的待审批请假数量: " + leaves.size());
        
        // 加载关联数据
        for (Leave leave : leaves) {
            if (leave.getApplicantId() != null) {
                User applicant = userRepository.findById(leave.getApplicantId())
                    .orElseThrow(() -> new BusinessException("申请人不存在"));
                leave.setApplicant(applicant);
                System.out.println("申请人信息: " + applicant.getName());
            }
        }
        
        return leaves;
    }
    
    @Override
    public List<Leave> getDepartmentLeaves(Integer deptId) {
        List<Leave> leaves = leaveRepository.findByDepartmentId(deptId);
        // 加载关联数据
        for (Leave leave : leaves) {
            if (leave.getApplicantId() != null) {
                leave.setApplicant(userRepository.findById(leave.getApplicantId()).orElse(null));
            }
            if (leave.getCurrentApproverId() != null) {
                leave.setCurrentApprover(userRepository.findById(leave.getCurrentApproverId()).orElse(null));
            }
        }
        return leaves;
    }
    
    @Override
    public Leave getLeaveById(Integer leaveId) {
        System.out.println("获取请假详情, ID: " + leaveId);
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new BusinessException("请假记录不存在"));
        
        // 确保关联数据已加载
        if (leave.getApplicantId() != null) {
            User applicant = userRepository.findById(leave.getApplicantId())
                .orElseThrow(() -> new BusinessException("申请人不存在"));
            leave.setApplicant(applicant);
            System.out.println("申请人信息: " + applicant.getName());
        }
        
        if (leave.getCurrentApproverId() != null) {
            User approver = userRepository.findById(leave.getCurrentApproverId())
                .orElseThrow(() -> new BusinessException("审批人不存在"));
            leave.setCurrentApprover(approver);
            System.out.println("审批人信息: " + approver.getName());
        }
        
        System.out.println("请假详情: " + leave);
        return leave;
    }
} 