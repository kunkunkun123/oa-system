package com.office.repository;

import com.office.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Integer> {
    @Query("SELECT l FROM Leave l WHERE l.applicantId = :applicantId ORDER BY l.createTime DESC")
    List<Leave> findByApplicantIdOrderByCreateTimeDesc(@Param("applicantId") Integer applicantId);
    
    @Query("SELECT l FROM Leave l WHERE l.currentApproverId = :approverId AND l.status IN (0, 1) ORDER BY l.createTime DESC")
    List<Leave> findByCurrentApproverIdOrderByCreateTimeDesc(@Param("approverId") Integer approverId);
    
    @Query("SELECT l FROM Leave l WHERE l.status = :status ORDER BY l.createTime DESC")
    List<Leave> findByStatus(@Param("status") Integer status);
    
    @Query("SELECT l FROM Leave l WHERE l.applicantId IN " +
           "(SELECT u.userId FROM User u WHERE u.deptId = :deptId) " +
           "ORDER BY l.createTime DESC")
    List<Leave> findByDepartmentId(@Param("deptId") Integer deptId);
    
    @Query("SELECT l FROM Leave l WHERE l.currentApproverId = :approverId AND l.status = :status ORDER BY l.createTime DESC")
    List<Leave> findByCurrentApproverIdAndStatus(@Param("approverId") Integer approverId, @Param("status") Integer status);
} 