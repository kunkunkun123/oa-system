package com.office.repository;

import com.office.entity.LeaveApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, Integer> {
    List<LeaveApproval> findByLeaveIdOrderByCreateTimeDesc(Integer leaveId);
} 