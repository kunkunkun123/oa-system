-- 用户表索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_dept_id ON users(dept_id);
CREATE INDEX idx_users_role_id ON users(role_id);

-- 任务表索引
CREATE INDEX idx_tasks_creator ON tasks(creator_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_tasks_dept ON tasks(dept_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- 消息表索引
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_receiver ON messages(receiver_id);
CREATE INDEX idx_messages_dept ON messages(dept_id);
CREATE INDEX idx_messages_create_time ON messages(create_time);