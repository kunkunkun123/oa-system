DELIMITER //

-- 发送消息的存储过程
CREATE PROCEDURE send_message(
    IN p_sender_id INT,
    IN p_receiver_id INT,
    IN p_dept_id INT,
    IN p_title VARCHAR(100),
    IN p_content TEXT,
    OUT p_message_id INT
)
BEGIN
    -- 检查接收者是否存在
    IF p_receiver_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM users WHERE user_id = p_receiver_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Receiver not found';
    END IF;
    
    -- 检查部门是否存在
    IF p_dept_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM departments WHERE dept_id = p_dept_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Department not found';
    END IF;
    
    -- 插入消息
    INSERT INTO messages (
        sender_id,
        receiver_id,
        dept_id,
        title,
        content,
        is_read,
        create_time
    ) VALUES (
        p_sender_id,
        p_receiver_id,
        p_dept_id,
        p_title,
        p_content,
        false,
        NOW()
    );
    
    SET p_message_id = LAST_INSERT_ID();
END //

-- 标记消息已读的存储过程
CREATE PROCEDURE mark_message_read(
    IN p_message_id INT,
    IN p_user_id INT
)
BEGIN
    DECLARE v_receiver_id INT;
    DECLARE v_dept_id INT;
    DECLARE v_user_dept_id INT;
    
    -- 获取消息信息
    SELECT receiver_id, dept_id 
    INTO v_receiver_id, v_dept_id
    FROM messages 
    WHERE message_id = p_message_id;
    
    -- 获取用户部门
    SELECT dept_id INTO v_user_dept_id
    FROM users
    WHERE user_id = p_user_id;
    
    -- 检查权限
    IF (v_receiver_id IS NOT NULL AND v_receiver_id != p_user_id)
       AND (v_dept_id IS NOT NULL AND v_dept_id != v_user_dept_id)
    THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No permission to mark this message as read';
    END IF;
    
    -- 标记已读
    UPDATE messages 
    SET is_read = true 
    WHERE message_id = p_message_id;
END //

DELIMITER ; 