package com.office.service.impl;

import com.office.entity.Task;
import com.office.entity.User;
import com.office.repository.TaskRepository;
import com.office.repository.UserRepository;
import com.office.service.TaskService;
import com.office.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public Task createTask(Task task) {
        // 验证必填字段
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new BusinessException("任务标题不能为空");
        }
        if (task.getAssigneeId() == null) {
            throw new BusinessException("请选择执行人");
        }
        if (task.getDueDate() == null) {
            throw new BusinessException("请设置截止日期");
        }

        // 验证执行人是否存在
        if (!userRepository.existsById(task.getAssigneeId())) {
            throw new BusinessException("执行人不存在");
        }
        
        // 检查被分配者是否存在
        if (task.getAssignee() != null) {
            User assignee = userRepository.findById(task.getAssignee().getUserId())
                .orElseThrow(() -> new BusinessException("被分配者不存在"));
            
            // 检查被分配者是否属于指定部门
            if (task.getDeptId() != null && 
                !assignee.getDeptId().equals(task.getDeptId())) {
                throw new BusinessException("被分配者不属于指定部门");
            }
            
            task.setAssignee(assignee);
        }
        
        // 设置初始状态
        if (task.getStatus() == null) {
            task.setStatus(0);
        }
        
        return taskRepository.save(task);
    }
    
    @Override
    public List<Task> getUserTasks(Integer userId) {
        return taskRepository.findByAssigneeId(userId);
    }
    
    @Override
    public List<Task> getDepartmentTasks(Integer deptId) {
        return taskRepository.findByDeptId(deptId);
    }
    
    @Override
    @Transactional
    public void updateTaskStatus(Integer taskId, Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("无效的任务状态");
        }
        Task task = getTaskById(taskId);
        task.setStatus(status);
        taskRepository.save(task);
    }
    
    @Override
    public Task getTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException("任务不存在"));
    }

    @Override
    public List<Task> getTasks() {
        System.out.println("获取所有任务");
        List<Task> tasks = taskRepository.findAllWithDetails();
        System.out.println("查询到的任务数量: " + tasks.size());
        tasks.forEach(task -> {
            System.out.println("任务ID: " + task.getTaskId());
            System.out.println("创建人ID: " + task.getCreatorId());
            System.out.println("执行人ID: " + task.getAssigneeId());
        });
        return tasks;
    }

    @Override
    public List<Task> getCurrentMonthTasks(Integer userId) {
        return taskRepository.findCurrentMonthTasks(userId);
    }
    
    @Override
    public Map<String, Object> getCurrentMonthTaskStats(Integer userId) {
        Long totalTasks = taskRepository.countCurrentMonthTasks(userId);
        Long completedTasks = taskRepository.countCurrentMonthCompletedTasks(userId);
        
        // 获取各状态任务数量
        List<Object[]> statusCounts = taskRepository.countCurrentMonthTasksByStatus(userId);
        Map<Integer, Long> statusMap = new HashMap<>();
        
        // 初始化所有状态的计数为0
        statusMap.put(0, 0L); // 待处理
        statusMap.put(1, 0L); // 进行中
        statusMap.put(2, 0L); // 已完成
        
        // 更新实际的计数
        for (Object[] result : statusCounts) {
            Integer status = (Integer) result[0];
            Long count = (Long) result[1];
            statusMap.put(status, count);
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalTasks);
        stats.put("completed", completedTasks);
        stats.put("pending", statusMap.get(0));     // 待处理
        stats.put("inProgress", statusMap.get(1));  // 进行中
        stats.put("completionRate", totalTasks > 0 ? 
            (double)completedTasks / totalTasks : 0);
            
        return stats;
    }
} 