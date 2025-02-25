package com.office.service.impl;

import com.office.entity.Message;
import com.office.entity.User;
import com.office.entity.Department;
import com.office.repository.MessageRepository;
import com.office.repository.UserRepository;
import com.office.service.MessageService;
import com.office.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<Message> getMyMessages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return messageRepository.findByReceiverIdOrDeptId(
            currentUser.getUserId(),
            currentUser.getDepartment().getDeptId()
        );
    }
    
    @Override
    @Transactional
    public Message sendMessage(Message message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        message.setSenderId(currentUser.getUserId());
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        
        return messageRepository.save(message);
    }
    
    @Override
    @Transactional
    public Message sendDepartmentMessage(Integer deptId, Message message) {
        message.setDeptId(deptId);
        message.setIsRead(false);
        return messageRepository.save(message);
    }
    
    @Override
    @Transactional
    public void markAsRead(Integer messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("消息不存在"));
        message.setIsRead(true);
        messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesByUserId(Integer userId) {
        return messageRepository.findByReceiverIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<Message> getSentMessages(Integer senderId) {
        return messageRepository.findBySenderIdOrderByCreateTimeDesc(senderId);
    }
} 