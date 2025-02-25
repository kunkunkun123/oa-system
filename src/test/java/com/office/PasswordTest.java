package com.office;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        
        // 生成新的加密密码
        String encodedPassword = encoder.encode(password);
        System.out.println("New encoded password: " + encodedPassword);
        
        // 验证密码匹配
        System.out.println("Password matches: " + encoder.matches(password, encodedPassword));
        
        // 验证数据库中的密码
        String storedPassword = "$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa";
        System.out.println("Stored password matches: " + encoder.matches(password, storedPassword));
        
        // 生成5个新的加密密码用于测试
        for (int i = 0; i < 5; i++) {
            System.out.println("Test password " + (i+1) + ": " + encoder.encode(password));
        }
    }
}