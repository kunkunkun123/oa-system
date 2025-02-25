package com.office.service;

import com.office.entity.User;
import com.office.repository.UserRepository;
import com.office.security.JwtTokenUtil;
import com.office.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testLogin_Success() {
        // 准备测试数据
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded_password");

        // 模拟依赖
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtTokenUtil.generateToken(user)).thenReturn("test_token");

        // 执行测试
        String result = userService.login("test", "password");

        // 验证结果
        assertEquals("test_token", result);
        verify(userRepository).findByUsername("test");
        verify(passwordEncoder).matches("password", "encoded_password");
        verify(jwtTokenUtil).generateToken(user);
    }
    
    @Test
    void loginFailUserNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(null);
        
        assertThrows(RuntimeException.class, () -> {
            userService.login("test", "password");
        });
    }
    
    @Test
    void loginFailWrongPassword() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("encoded_password");
        
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);
        
        assertThrows(RuntimeException.class, () -> {
            userService.login("test", "wrong_password");
        });
    }
} 