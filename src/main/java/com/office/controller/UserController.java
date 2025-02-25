package com.office.controller;

import com.office.dto.request.LoginRequest;
import com.office.dto.request.PasswordUpdateRequest;
import com.office.dto.response.LoginResponse;
import com.office.dto.response.ErrorResponse;
import com.office.entity.User;
import com.office.security.JwtTokenUtil;
import com.office.service.UserService;
import com.office.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("接收到登录请求: " + request.getUsername());
            
            // 获取 token
            String token = userService.login(request.getUsername(), request.getPassword());
            
            // 从 token 中获取用户信息
            Integer userId = jwtTokenUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);
            
            // 返回登录响应
            return ResponseEntity.ok(new LoginResponse(token, user));
        } catch (BusinessException e) {
            System.out.println("登录失败: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("登录失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            System.out.println("接收到的注册数据: " + user);
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(
            @RequestBody PasswordUpdateRequest request,
            @RequestAttribute Integer currentUserId) {
        userService.updatePassword(currentUserId, 
                                 request.getOldPassword(), 
                                 request.getNewPassword());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
        @PathVariable Integer userId,
        @RequestBody User user) {
        try {
            user.setUserId(userId);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Integer userId) {
        try {
            userService.resetPassword(userId);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateStatus(
        @PathVariable Integer userId,
        @RequestBody Map<String, Boolean> status) {
        try {
            userService.updateStatus(userId, status.get("status"));
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    // 其他接口...
} 