DELIMITER //

-- 任务分配触发器
CREATE TRIGGER before_task_insert
BEFORE INSERT ON tasks
FOR EACH ROW
BEGIN
    DECLARE v_assignee_dept_id INT;
    
    -- 获取执行人部门
    SELECT dept_id INTO v_assignee_dept_id
    FROM users
    WHERE user_id = NEW.assignee_id;
    
    -- 如果指定了部门,检查执行人是否属于该部门
    IF NEW.dept_id IS NOT NULL AND v_assignee_dept_id != NEW.dept_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Assignee must belong to the specified department';
    END IF;
END //

-- 消息发送触发器
CREATE TRIGGER before_message_insert
BEFORE INSERT ON messages
FOR EACH ROW
BEGIN
    -- 检查发送者是否存在且未被禁用
    IF NOT EXISTS (
        SELECT 1 FROM users 
        WHERE user_id = NEW.sender_id 
        AND status = true
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid sender';
    END IF;
    
    -- 如果是部门消息,检查发送者是否是部门经理或管理员
    IF NEW.dept_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM users u
        WHERE u.user_id = NEW.sender_id
        AND (
            u.role_id = 1 -- 管理员
            OR (u.role_id = 3 -- 部门经理
                AND EXISTS (
                    SELECT 1 FROM departments d
                    WHERE d.dept_id = NEW.dept_id
                    AND d.manager_id = u.user_id
                )
            )
        )
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No permission to send department message';
    END IF;
END //

DELIMITER ; 