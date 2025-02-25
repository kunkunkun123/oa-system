package com.office.service.impl;

import com.office.entity.Department;
import com.office.entity.User;
import com.office.repository.DepartmentRepository;
import com.office.repository.UserRepository;
import com.office.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    
    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    @Override
    @CacheEvict(value = "departments", allEntries = true)
    @Transactional
    public Department updateDepartment(Department department) {
        if (!departmentRepository.existsById(department.getDeptId())) {
            throw new RuntimeException("部门不存在");
        }
        return departmentRepository.save(department);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(Integer deptId) {
        if (!departmentRepository.existsById(deptId)) {
            throw new RuntimeException("部门不存在");
        }
        departmentRepository.deleteById(deptId);
    }
    
    @Override
    @Cacheable(value = "departments", key = "#deptId")
    public Department getDepartmentById(Integer deptId) {
        return departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("部门不存在"));
    }
    
    @Override
    @Cacheable(value = "departments")
    public List<Department> getAllDepartments() {
        // 使用单次查询获取部门及其经理信息
        List<Department> departments = departmentRepository.findAllWithManager();
        
        // 仅在调试时打印信息
        if (log.isDebugEnabled()) {
            departments.forEach(dept -> {
                log.debug("部门：{}, 经理：{}", 
                    dept.getDeptName(), 
                    dept.getManager() != null ? dept.getManager().getName() : "暂无");
            });
        }
        
        return departments;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)
    public void assignManager(Integer deptId, Integer managerId) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("部门不存在"));
            
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
            
        if (manager.getRoleId() != 3) {
            throw new RuntimeException("只有部门经理角色才能被指定为部门经理");
        }
            
        if (manager.getDeptId() != null && !manager.getDeptId().equals(deptId)) {
            throw new RuntimeException("管理员必须属于该部门");
        }
        
        // 更新部门信息
        department.setManagerId(managerId);
        department.setUpdateTime(LocalDateTime.now());
        departmentRepository.save(department);
        
        // 更新用户部门
        if (manager.getDeptId() == null) {
            manager.setDeptId(deptId);
            userRepository.save(manager);
        }
        
        // 清除缓存
        log.info("清除部门缓存");
    }
} 