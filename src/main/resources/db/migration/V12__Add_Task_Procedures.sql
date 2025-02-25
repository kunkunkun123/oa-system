DELIMITER //

-- 创建任务的存储过程
CREATE PROCEDURE create_task(
    IN p_title VARCHAR(100),
    IN p_content TEXT,
    IN p_creator_id INT,
    IN p_assignee_id INT,
    IN p_dept_id INT,
    IN p_due_date DATE,
    OUT p_task_id INT
)
BEGIN
    DECLARE v_assignee_dept_id INT;
    
    -- 检查执行人是否存在且属于指定部门
    SELECT dept_id INTO v_assignee_dept_id
    FROM users 
    WHERE user_id = p_assignee_id;
    
    IF v_assignee_dept_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Assignee not found';
    END IF;
    
    IF p_dept_id IS NOT NULL AND v_assignee_dept_id != p_dept_id THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Assignee must belong to the specified department';
    END IF;
    
    INSERT INTO tasks (
        title,
        content,
        creator_id,
        assignee_id,
        dept_id,
        status,
        due_date,
        create_time
    ) VALUES (
        p_title,
        p_content,
        p_creator_id,
        p_assignee_id,
        p_dept_id,
        0,
        p_due_date,
        NOW()
    );
    
    SET p_task_id = LAST_INSERT_ID();
END //

-- 更新任务状态的存储过程
CREATE PROCEDURE update_task_status(
    IN p_task_id INT,
    IN p_status INT,
    IN p_user_id INT
)
BEGIN
    DECLARE v_assignee_id INT;
    DECLARE v_creator_id INT;
    DECLARE v_user_role_id INT;
    
    -- 获取任务信息
    SELECT assignee_id, creator_id 
    INTO v_assignee_id, v_creator_id
    FROM tasks 
    WHERE task_id = p_task_id;
    
    -- 获取用户角色
    SELECT role_id INTO v_user_role_id
    FROM users
    WHERE user_id = p_user_id;
    
    -- 检查权限
    IF v_user_role_id != 1 -- 不是管理员
       AND p_user_id != v_assignee_id -- 不是执行人
       AND p_user_id != v_creator_id -- 不是创建人
    THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No permission to update task status';
    END IF;
    
    -- 更新状态
    UPDATE tasks 
    SET status = p_status,
        update_time = NOW()
    WHERE task_id = p_task_id;
END //

DELIMITER ; 