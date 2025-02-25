package com.office.repository;

import com.office.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.manager")
    List<Department> findAllWithManager();
    
    boolean existsByDeptName(String deptName);
    
    @Query("SELECT d FROM Department d WHERE d.manager.userId = :managerId")
    List<Department> findByManagerId(@Param("managerId") Integer managerId);
} 