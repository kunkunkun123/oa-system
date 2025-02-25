package com.office.service.impl;

import com.office.entity.User;
import com.office.repository.UserRepository;
import com.office.security.JwtTokenUtil;
import com.office.service.UserService;
import com.office.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.List;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import com.office.entity.Department;
import com.office.repository.DepartmentRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Override
    public String login(String username, String password) {
        System.out.println("开始处理登录请求 - 用户名: " + username);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查账号状态
        if (!user.getStatus()) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 特殊处理默认密码
        boolean passwordMatches = false;
        if ("123456".equals(password) && 
            "$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa".equals(user.getPassword())) {
            passwordMatches = true;
        } else {
            passwordMatches = passwordEncoder.matches(password, user.getPassword());
        }
        
        if (!passwordMatches) {
            throw new BusinessException("密码错误");
        }
        
        return jwtTokenUtil.generateToken(user);
    }
    
    @Override
    @Transactional
    public User register(User user) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 确保必填字段不为空
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }
        
        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(true);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 如果没有设置角色，设置默认角色
        if (user.getRoleId() == null) {
            user.setRoleId(2); // 假设 2 是普通用户角色
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        // 获取现有用户
        User existingUser = getUserById(user.getUserId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 保持一些字段不变
        user.setPassword(existingUser.getPassword());
        user.setUsername(existingUser.getUsername());
        user.setStatus(existingUser.getStatus());
        user.setCreateTime(existingUser.getCreateTime());
        user.setUpdateTime(LocalDateTime.now());

        // 验证必填字段
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }

        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = getUserById(userId);
        // 检查是否是部门经理
        if (user.getRoleId() == 3) {
            // 检查是否有管理的部门
            List<Department> departments = departmentRepository.findByManagerId(userId);
            if (!departments.isEmpty()) {
                throw new BusinessException("该用户是部门经理，请先解除其管理的部门");
            }
        }
        userRepository.deleteById(userId);
    }
    
    @Override
    @Transactional
    public void updatePassword(Integer userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Override
    public List<User> getUsersByDepartment(Integer deptId) {
        return userRepository.findByDeptId(deptId);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("ROLE_USER")
            .build();
    }
    
    @PostConstruct
    public void init() {
        // 重置 admin 用户密码
        try {
            User admin = userRepository.findByUsername("admin");
            if (admin != null) {
                admin.setPassword("$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa");
                userRepository.save(admin);
                System.out.println("\n=== admin 用户密码已重置 ===");
            }
        } catch (Exception e) {
            System.out.println("重置密码失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void resetPassword(Integer userId) {
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode("123456")); // 重置为默认密码
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void updateStatus(Integer userId, Boolean status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
    }
    
    // 其他方法实现...
} 