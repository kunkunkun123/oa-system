-- 用户详情视图
CREATE VIEW user_details AS
SELECT 
    u.*,
    d.dept_name,
    r.role_name,
    r.role_code,
    dm.name as manager_name
FROM users u
LEFT JOIN departments d ON u.dept_id = d.dept_id
LEFT JOIN user_roles r ON u.role_id = r.role_id
LEFT JOIN users dm ON d.manager_id = dm.user_id;

-- 部门统计视图
CREATE VIEW department_stats AS
SELECT 
    d.dept_id,
    d.dept_name,
    COUNT(u.user_id) as user_count,
    u.name as manager_name,
    COUNT(t.task_id) as task_count,
    SUM(CASE WHEN t.status = 2 THEN 1 ELSE 0 END) as completed_tasks
FROM departments d
LEFT JOIN users u ON d.manager_id = u.user_id
LEFT JOIN tasks t ON d.dept_id = t.dept_id
GROUP BY d.dept_id;