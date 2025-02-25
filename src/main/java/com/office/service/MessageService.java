package com.office.service;

import com.office.entity.Message;
import java.util.List;

public interface MessageService {
    List<Message> getMyMessages();
    Message sendMessage(Message message);
    Message sendDepartmentMessage(Integer deptId, Message message);
    void markAsRead(Integer messageId);
    List<Message> getMessagesByUserId(Integer userId);
    List<Message> getSentMessages(Integer senderId);
} 