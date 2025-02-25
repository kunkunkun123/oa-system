DELIMITER //

CREATE PROCEDURE register_user(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(100),
    IN p_name VARCHAR(50),
    IN p_role_id INT,
    IN p_dept_id INT,
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    OUT p_user_id INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Registration failed';
    END;

    START TRANSACTION;
    
    -- 检查用户名是否已存在
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Username already exists';
    END IF;
    
    -- 检查部门是否存在
    IF p_dept_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM departments WHERE dept_id = p_dept_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Department does not exist';
    END IF;
    
    -- 检查角色是否存在
    IF NOT EXISTS (SELECT 1 FROM user_roles WHERE role_id = p_role_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid role';
    END IF;
    
    -- 插入新用户
    INSERT INTO users (
        username, 
        password,
        name,
        role_id,
        dept_id,
        email,
        phone,
        status,
        create_time
    ) VALUES (
        p_username,
        p_password,
        p_name,
        p_role_id,
        p_dept_id,
        p_email,
        p_phone,
        true,
        NOW()
    );
    
    SET p_user_id = LAST_INSERT_ID();
    
    COMMIT;
END //

DELIMITER ; 