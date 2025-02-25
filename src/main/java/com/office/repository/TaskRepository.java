package com.office.repository;

import com.office.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByAssigneeUserId(Integer userId);
    
    List<Task> findByDeptId(Integer deptId);
    
    @Query("SELECT t FROM Task t WHERE t.assignee.userId = ?1 AND DATE(t.dueDate) = CURRENT_DATE")
    List<Task> findTodayTasksByAssigneeId(Integer assigneeId);
    
    List<Task> findByCreator_UserIdOrderByCreateTimeDesc(Integer creatorId);

    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.creator " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.department")
    List<Task> findAllWithDetails();

    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.creator " +
           "LEFT JOIN FETCH t.assignee " +
           "LEFT JOIN FETCH t.department " +
           "WHERE t.assigneeId = :userId")
    List<Task> findByAssigneeId(@Param("userId") Integer userId);

    @Query("SELECT t FROM Task t WHERE t.assigneeId = :userId " +
           "AND FUNCTION('DATE_FORMAT', t.createTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    List<Task> findCurrentMonthTasks(@Param("userId") Integer userId);
    
    @Query("SELECT t FROM Task t WHERE t.assigneeId = :userId " +
           "AND FUNCTION('DATE_FORMAT', t.createTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m') " +
           "AND t.status = 2")
    List<Task> findCurrentMonthCompletedTasks(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assigneeId = :userId " +
           "AND FUNCTION('DATE_FORMAT', t.createTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    Long countCurrentMonthTasks(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assigneeId = :userId " +
           "AND FUNCTION('DATE_FORMAT', t.createTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m') " +
           "AND t.status = 2")
    Long countCurrentMonthCompletedTasks(@Param("userId") Integer userId);
    
    @Query("SELECT t.status, COUNT(t) FROM Task t " +
           "WHERE t.assigneeId = :userId " +
           "AND FUNCTION('DATE_FORMAT', t.createTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m') " +
           "GROUP BY t.status")
    List<Object[]> countCurrentMonthTasksByStatus(@Param("userId") Integer userId);
} 