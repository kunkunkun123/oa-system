package com.office.mapper;

import com.office.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

@Mapper
public interface UserMapper {
    // ... 其他方法 ...
    
    @Select("CALL register_user(#{username}, #{password}, #{name}, #{roleId}, #{deptId}, #{email}, #{phone}, #{userId, mode=OUT, jdbcType=INTEGER})")
    @Options(statementType = StatementType.CALLABLE)
    void registerUser(User user);
    
    @Select("SELECT u.*, d.dept_name, r.role_name " +
            "FROM users u " +
            "LEFT JOIN departments d ON u.dept_id = d.dept_id " +
            "LEFT JOIN user_roles r ON u.role_id = r.role_id " +
            "WHERE u.user_id = #{userId}")
    @Results({
        @Result(property = "userId", column = "user_id"),
        @Result(property = "roleId", column = "role_id"),
        @Result(property = "deptId", column = "dept_id"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "department.deptName", column = "dept_name"),
        @Result(property = "userRole.roleName", column = "role_name")
    })
    User findById(Integer userId);
} 