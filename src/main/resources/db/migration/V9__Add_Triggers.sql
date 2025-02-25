DELIMITER //

-- 部门经理更新触发器
CREATE TRIGGER before_department_manager_update
BEFORE UPDATE ON departments
FOR EACH ROW
BEGIN
    -- 检查新经理是否为部门经理角色
    IF NEW.manager_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM users 
        WHERE user_id = NEW.manager_id 
        AND role_id = 3
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Manager must have department manager role';
    END IF;
END //

-- 用户删除触发器
CREATE TRIGGER before_user_delete
BEFORE DELETE ON users
FOR EACH ROW
BEGIN
    -- 检查是否为部门经理
    IF EXISTS (
        SELECT 1 FROM departments 
        WHERE manager_id = OLD.user_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete user who is a department manager';
    END IF;
END //

DELIMITER ;