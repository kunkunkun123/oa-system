package com.office.controller;

import com.office.entity.Message;
import com.office.entity.User;
import com.office.service.MessageService;
import com.office.service.UserService;
import com.office.dto.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyMessages(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
            Integer userId = claims.get("userId", Integer.class);
            List<Message> messages = messageService.getMessagesByUserId(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "获取消息失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> sendMessage(
            @RequestAttribute Integer currentUserId,
            @RequestBody Message message) {
        User sender = userService.getUserById(currentUserId);
        message.setSender(sender);
        return ResponseEntity.ok(messageService.sendMessage(message));
    }
    
    @PostMapping("/department/{deptId}")
    public ResponseEntity<?> sendDepartmentMessage(
            @RequestAttribute Integer currentUserId,
            @PathVariable Integer deptId,
            @RequestBody Message message) {
        User sender = userService.getUserById(currentUserId);
        message.setSender(sender);
        return ResponseEntity.ok(messageService.sendDepartmentMessage(deptId, message));
    }
    
    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markAsRead(
            @RequestAttribute Integer currentUserId,
            @PathVariable Integer messageId) {
        try {
            if (messageId == null) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("消息ID不能为空"));
            }
            messageService.markAsRead(messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("标记已读失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/sent")
    public ResponseEntity<?> getSentMessages(@RequestAttribute Integer currentUserId) {
        List<Message> messages = messageService.getSentMessages(currentUserId);
        return ResponseEntity.ok(messages);
    }
} 