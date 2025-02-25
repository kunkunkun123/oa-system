package com.office.service;

import com.office.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    String login(String username, String password);
    User register(User user);
    void updatePassword(Integer userId, String oldPassword, String newPassword);
    List<User> getAllUsers();
    User getUserById(Integer userId);
    User updateUser(User user);
    void deleteUser(Integer userId);
    List<User> getUsersByDepartment(Integer deptId);
    void resetPassword(Integer userId);
    void updateStatus(Integer userId, Boolean status);
} 