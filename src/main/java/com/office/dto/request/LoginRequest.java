package com.office.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    
    // 添加 toString 方法，但不包含密码
    @Override
    public String toString() {
        return "LoginRequest{" +
            "username='" + username + '\'' +
            ", password='[PROTECTED]'" +
            '}';
    }
} 