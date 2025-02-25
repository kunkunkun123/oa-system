package com.office.repository;

import com.office.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findByDeptId(Integer deptId);
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u " +
           "JOIN Department d ON d.managerId = u.userId " +
           "WHERE d.deptId = :deptId")
    Optional<User> findDepartmentManager(@Param("deptId") Integer deptId);
    
    List<User> findByRoleId(Integer roleId);
} 