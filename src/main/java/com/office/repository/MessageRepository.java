package com.office.repository;

import com.office.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.receiver.userId = :receiverId OR m.deptId = :deptId")
    List<Message> findByReceiverIdOrDeptId(
        @Param("receiverId") Integer receiverId, 
        @Param("deptId") Integer deptId
    );
    
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    void markAsRead(@Param("messageId") Integer messageId);
    
    List<Message> findBySenderUserIdOrderByCreateTimeDesc(Integer userId);

    List<Message> findByReceiverIdOrderByCreateTimeDesc(Integer receiverId);

    List<Message> findByReceiverIdOrDeptIdOrderByCreateTimeDesc(Integer receiverId, Integer deptId);

    List<Message> findBySenderIdOrderByCreateTimeDesc(Integer senderId);
} 