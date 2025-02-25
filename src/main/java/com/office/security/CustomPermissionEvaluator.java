package com.office.security;

import com.office.entity.User;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        User user = (User) authentication.getPrincipal();
        
        // 系统管理员拥有所有权限
        if (user.getRoleId() == 1) {
            return true;
        }
        
        // 总经理拥有除系统管理外的所有权限
        if (user.getRoleId() == 2) {
            return !"ADMIN".equals(permission);
        }
        
        // 部门经理只能管理本部门
        if (user.getRoleId() == 3) {
            if (targetDomainObject instanceof Integer) {
                return user.getDeptId().equals(targetDomainObject);
            }
        }
        
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
} 