package com.office.controller;

import com.office.entity.Department;
import com.office.service.DepartmentService;
import com.office.dto.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    
    @Autowired
    private DepartmentService departmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(departmentService.createDepartment(department));
    }
    
    @PutMapping("/{deptId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Integer deptId,
            @RequestBody Department department) {
        department.setDeptId(deptId);
        return ResponseEntity.ok(departmentService.updateDepartment(department));
    }
    
    @DeleteMapping("/{deptId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable Integer deptId) {
        try {
            departmentService.deleteDepartment(deptId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/{deptId}")
    public ResponseEntity<?> getDepartment(@PathVariable Integer deptId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(deptId));
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDepartments() {
        System.out.println("获取所有部门");
        List<Department> departments = departmentService.getAllDepartments();
        System.out.println("部门列表: " + departments);
        return ResponseEntity.ok(departments);
    }
    
    @PutMapping("/{deptId}/manager/{managerId}")
    public ResponseEntity<?> assignManager(
            @PathVariable Integer deptId,
            @PathVariable Integer managerId) {
        departmentService.assignManager(deptId, managerId);
        return ResponseEntity.ok().build();
    }
} 