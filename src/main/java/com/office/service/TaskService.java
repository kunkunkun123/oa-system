package com.office.service;

import com.office.entity.Task;
import java.util.List;
import java.util.Map;

public interface TaskService {
    Task createTask(Task task);
    List<Task> getUserTasks(Integer userId);
    List<Task> getDepartmentTasks(Integer deptId);
    void updateTaskStatus(Integer taskId, Integer status);
    Task getTaskById(Integer taskId);
    List<Task> getTasks();
    List<Task> getCurrentMonthTasks(Integer userId);
    Map<String, Object> getCurrentMonthTaskStats(Integer userId);
} 