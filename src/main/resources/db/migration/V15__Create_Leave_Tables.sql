-- 请假表
CREATE TABLE leaves (
    id INT PRIMARY KEY AUTO_INCREMENT,
    applicant_id INT NOT NULL,
    type INT NOT NULL COMMENT '1:事假,2:病假,3:年假,4:调休',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status INT NOT NULL DEFAULT 0 COMMENT '0:待审批,1:审批中,2:已通过,3:已拒绝',
    current_approver_id INT,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    FOREIGN KEY (applicant_id) REFERENCES users(user_id),
    FOREIGN KEY (current_approver_id) REFERENCES users(user_id)
);

-- 请假审批记录表
CREATE TABLE leave_approvals (
    id INT PRIMARY KEY AUTO_INCREMENT,
    leave_id INT NOT NULL,
    approver_id INT NOT NULL,
    status INT NOT NULL COMMENT '1:通过,2:拒绝',
    comment VARCHAR(200),
    create_time DATETIME NOT NULL,
    FOREIGN KEY (leave_id) REFERENCES leaves(id),
    FOREIGN KEY (approver_id) REFERENCES users(user_id)
);

-- 创建审批流程触发器
DELIMITER //

CREATE TRIGGER after_leave_approval
AFTER INSERT ON leave_approvals
FOR EACH ROW
BEGIN
    DECLARE v_dept_id INT;
    DECLARE v_manager_id INT;
    DECLARE v_applicant_role INT;
    
    -- 获取申请人部门和角色信息
    SELECT u.dept_id, u.role_id 
    INTO v_dept_id, v_applicant_role
    FROM leaves l
    JOIN users u ON l.applicant_id = u.user_id
    WHERE l.id = NEW.leave_id;
    
    -- 获取部门经理ID
    SELECT manager_id INTO v_manager_id
    FROM departments
    WHERE dept_id = v_dept_id;
    
    -- 如果是拒绝，直接更新请假状态为拒绝
    IF NEW.status = 2 THEN
        UPDATE leaves 
        SET status = 3,
            update_time = NOW(),
            current_approver_id = NULL
        WHERE id = NEW.leave_id;
    ELSE
        -- 如果是部门经理审批通过
        IF NEW.approver_id = v_manager_id THEN
            UPDATE leaves 
            SET status = 2,
                update_time = NOW(),
                current_approver_id = NULL
            WHERE id = NEW.leave_id;
        -- 如果是上级审批通过，更新为待部门经理审批
        ELSE
            UPDATE leaves 
            SET status = 1,
                update_time = NOW(),
                current_approver_id = v_manager_id
            WHERE id = NEW.leave_id;
        END IF;
    END IF;
END //

DELIMITER ; 