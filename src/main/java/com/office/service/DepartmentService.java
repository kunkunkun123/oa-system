package com.office.service;

import com.office.entity.Department;
import java.util.List;

public interface DepartmentService {
    Department createDepartment(Department department);
    Department updateDepartment(Department department);
    void deleteDepartment(Integer deptId);
    Department getDepartmentById(Integer deptId);
    List<Department> getAllDepartments();
    void assignManager(Integer deptId, Integer managerId);
} 