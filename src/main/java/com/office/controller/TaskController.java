package com.office.controller;

import com.office.dto.request.TaskStatusRequest;
import com.office.entity.Task;
import com.office.entity.User;
import com.office.service.TaskService;
import com.office.service.UserService;
import com.office.exception.BusinessException;
import com.office.dto.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestAttribute Integer currentUserId) {
        try {
            // 设置创建人ID
            task.setCreatorId(currentUserId);
            // 设置初始状态为未开始
            task.setStatus(0);
            // 设置创建时间
            task.setCreateTime(LocalDateTime.now());
            
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.ok(createdTask);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyTasks(@RequestAttribute Integer currentUserId) {
        try {
            System.out.println("获取用户任务, userId: " + currentUserId);
            return ResponseEntity.ok(taskService.getUserTasks(currentUserId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/department/{deptId}")
    public ResponseEntity<?> getDepartmentTasks(@PathVariable Integer deptId) {
        return ResponseEntity.ok(taskService.getDepartmentTasks(deptId));
    }
    
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody TaskStatusRequest request) {
        try {
            taskService.updateTaskStatus(taskId, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        try {
            System.out.println("获取所有任务");
            return ResponseEntity.ok(taskService.getTasks());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/monthly-stats")
    public ResponseEntity<?> getMonthlyTaskStats(@RequestAttribute Integer currentUserId) {
        try {
            return ResponseEntity.ok(taskService.getCurrentMonthTaskStats(currentUserId));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
} 