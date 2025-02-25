-- 清空现有数据（按照外键约束的顺序）
DELETE FROM messages;
DELETE FROM tasks;
UPDATE departments SET manager_id = NULL;
DELETE FROM users;
DELETE FROM user_roles;

-- 插入角色数据
INSERT INTO user_roles (role_id, role_name, role_code) VALUES
(1, '系统管理员', 'ADMIN'),
(2, '总经理', 'MANAGER'),
(3, '部门经理', 'DEPT_MANAGER'),
(4, '普通员工', 'EMPLOYEE');

-- 插入部门数据
INSERT INTO departments (dept_name) VALUES
('技术部'),
('市场部'),
('人事部'),
('财务部'),
('运营部'),
('客服部');

-- 插入用户数据（密码都是123456的加密形式）
INSERT INTO users (username, password, name, role_id, dept_id, email, phone, status, create_time) VALUES
-- 管理员
('admin', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '系统管理员', 1, NULL, 'admin@company.com', '13800000001', true, NOW()),
-- 总经理
('zhangsan', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '张三', 2, NULL, 'zhangsan@company.com', '13800000002', true, NOW()),
-- 部门经理
('lisi', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '李四', 3, 1, 'lisi@company.com', '13800000003', true, NOW()),
('wangwu', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '王五', 3, 2, 'wangwu@company.com', '13800000004', true, NOW()),
('zhaoliu', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '赵六', 3, 3, 'zhaoliu@company.com', '13800000005', true, NOW()),
-- 普通员工（技术部）
('tech1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术一', 4, 1, 'tech1@company.com', '13800000006', true, NOW()),
('tech2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术二', 4, 1, 'tech2@company.com', '13800000007', true, NOW()),
('tech3', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术三', 4, 1, 'tech3@company.com', '13800000008', true, NOW()),
-- 普通员工（市场部）
('market1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '市场一', 4, 2, 'market1@company.com', '13800000009', true, NOW()),
('market2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '市场二', 4, 2, 'market2@company.com', '13800000010', true, NOW()),
-- 普通员工（人事部）
('hr1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '人事一', 4, 3, 'hr1@company.com', '13800000011', true, NOW()),
('hr2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '人事二', 4, 3, 'hr2@company.com', '13800000012', true, NOW());

-- 设置部门经理
UPDATE departments SET manager_id = (SELECT user_id FROM users WHERE username = 'lisi') WHERE dept_name = '技术部';
UPDATE departments SET manager_id = (SELECT user_id FROM users WHERE username = 'wangwu') WHERE dept_name = '市场部';
UPDATE departments SET manager_id = (SELECT user_id FROM users WHERE username = 'zhaoliu') WHERE dept_name = '人事部';

-- 插入任务数据
INSERT INTO tasks (title, content, creator_id, assignee_id, dept_id, status, due_date, create_time) VALUES
-- 技术部任务
('系统升级计划', '制定下一季度系统升级计划', 
 (SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech1'),
 1, 0, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), NOW()),
 
('代码重构', '重构用户管理模块代码', 
 (SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech2'),
 1, 1, DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), NOW()),
 
('性能优化', '优化系统响应速度', 
 (SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech3'),
 1, 2, DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), NOW()),

-- 市场部任务
('市场调研', '进行竞品分析和市场调研', 
 (SELECT user_id FROM users WHERE username = 'wangwu'),
 (SELECT user_id FROM users WHERE username = 'market1'),
 2, 0, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), NOW()),
 
('营销方案', '制定Q2营销推广方案', 
 (SELECT user_id FROM users WHERE username = 'wangwu'),
 (SELECT user_id FROM users WHERE username = 'market2'),
 2, 1, DATE_ADD(CURRENT_DATE, INTERVAL 6 DAY), NOW()),

-- 人事部任务
('员工培训', '组织新员工入职培训', 
 (SELECT user_id FROM users WHERE username = 'zhaoliu'),
 (SELECT user_id FROM users WHERE username = 'hr1'),
 3, 0, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), NOW()),
 
('绩效考核', '进行月度绩效考核', 
 (SELECT user_id FROM users WHERE username = 'zhaoliu'),
 (SELECT user_id FROM users WHERE username = 'hr2'),
 3, 2, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), NOW());

-- 插入消息数据
INSERT INTO messages (sender_id, receiver_id, dept_id, title, content, is_read, create_time) VALUES
-- 系统消息
((SELECT user_id FROM users WHERE username = 'admin'),
 NULL,
 NULL,
 '系统维护通知',
 '系统将于本周六晚上进行例行维护，请提前做好相关工作安排。',
 false,
 NOW()),

-- 部门消息
((SELECT user_id FROM users WHERE username = 'lisi'),
 NULL,
 1,
 '技术部周会',
 '请各位同事准时参加周一上午10点的部门周会。',
 false,
 NOW()),

-- 个人消息
((SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech1'),
 NULL,
 '代码审查',
 '请对用户管理模块的代码进行审查。',
 false,
 NOW()),

((SELECT user_id FROM users WHERE username = 'wangwu'),
 (SELECT user_id FROM users WHERE username = 'market1'),
 NULL,
 '报告提交',
 '请尽快提交市场调研报告。',
 true,
 NOW()),

((SELECT user_id FROM users WHERE username = 'zhaoliu'),
 (SELECT user_id FROM users WHERE username = 'hr1'),
 NULL,
 '培训材料',
 '请准备下周的新员工培训材料。',
 false,
 NOW());

-- 添加一些已完成的历史任务
INSERT INTO tasks (title, content, creator_id, assignee_id, dept_id, status, due_date, create_time) VALUES
-- 已完成的技术部任务
('数据库优化', '优化数据库查询性能', 
 (SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech1'),
 1, 2, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY)),
 
('接口开发', '开发新的API接口', 
 (SELECT user_id FROM users WHERE username = 'lisi'),
 (SELECT user_id FROM users WHERE username = 'tech2'),
 1, 2, DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)),

-- 已完成的市场部任务
('市场报告', '编写市场分析报告', 
 (SELECT user_id FROM users WHERE username = 'wangwu'),
 (SELECT user_id FROM users WHERE username = 'market1'),
 2, 2, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY)); 