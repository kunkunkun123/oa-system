package com.office.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.StatementType;
import com.office.entity.Task;

@Mapper
public interface TaskMapper {
    @Select("CALL create_task(#{title}, #{content}, #{creatorId}, #{assigneeId}, #{deptId}, #{dueDate}, #{taskId, mode=OUT, jdbcType=INTEGER})")
    @Options(statementType = StatementType.CALLABLE)
    void createTask(Task task);
    
    @Select("CALL update_task_status(#{taskId}, #{status}, #{userId})")
    @Options(statementType = StatementType.CALLABLE)
    void updateTaskStatus(@Param("taskId") Integer taskId, @Param("status") Integer status, @Param("userId") Integer userId);
    
    // ... 其他方法
} 