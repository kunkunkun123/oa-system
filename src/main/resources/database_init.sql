-- 创建数据库
CREATE DATABASE IF NOT EXISTS oa_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE oa_system;

-- 创建用户角色表
CREATE TABLE IF NOT EXISTS user_roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建部门表
CREATE TABLE IF NOT EXISTS departments (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL,
    manager_id INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dept_name (dept_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role_id INT,
    dept_id INT,
    email VARCHAR(100),
    phone VARCHAR(20),
    status BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username),
    FOREIGN KEY (role_id) REFERENCES user_roles(role_id),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 添加部门表的外键约束
ALTER TABLE departments
ADD FOREIGN KEY (manager_id) REFERENCES users(user_id);

-- 创建任务表
CREATE TABLE IF NOT EXISTS tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    creator_id INT NOT NULL,
    assignee_id INT NOT NULL,
    dept_id INT,
    status INT DEFAULT 0 COMMENT '0: 待处理, 1: 进行中, 2: 已完成',
    due_date DATE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(user_id),
    FOREIGN KEY (assignee_id) REFERENCES users(user_id),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建消息表
CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    receiver_id INT,
    dept_id INT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id),
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建公告表
CREATE TABLE IF NOT EXISTS announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    creator_id INT NOT NULL,
    create_time DATETIME NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已下线，1-已发布',
    FOREIGN KEY (creator_id) REFERENCES users(user_id)
);

-- 插入初始角色数据
INSERT IGNORE INTO user_roles (role_id, role_name, role_code) VALUES
    (1, '系统管理员', 'ADMIN'),
    (2, '总经理', 'MANAGER'),
    (3, '部门经理', 'DEPT_MANAGER'),
    (4, '普通员工', 'EMPLOYEE');

-- 插入初始管理员用户
INSERT IGNORE INTO users (user_id, username, password, name, role_id, email, phone, create_time) VALUES
    (1, 'admin', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '系统管理员', 1, 'admin@company.com', '13800000000', NOW()); 