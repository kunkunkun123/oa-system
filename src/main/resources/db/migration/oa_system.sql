/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : oa_system

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 25/02/2025 08:44:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `announcement_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `creator_id` int(11) NOT NULL COMMENT '公告ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：0-已下线，1-已发布',
  `id` int(11) NOT NULL COMMENT 'ID',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`announcement_id`) USING BTREE,
  INDEX `creator_id`(`creator_id`) USING BTREE,
  CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------
INSERT INTO `announcements` VALUES (1, '请注意代办事项', '请注意代办事项请注意代办事项', 36, '2025-02-19 02:53:40', 1, 0, NULL);
INSERT INTO `announcements` VALUES (2, '放假通知', '1.20-1.22日放假', 36, '2025-02-19 21:49:12', 1, 0, NULL);

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `dept_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `manager_id` int(11) NULL DEFAULT NULL COMMENT '部门经理ID',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `manager_id`(`manager_id`) USING BTREE,
  CONSTRAINT `departments_ibfk_1` FOREIGN KEY (`manager_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES (1, '人力资源部', NULL, '2025-02-18 22:14:56', '2025-02-19 02:27:13');
INSERT INTO `departments` VALUES (2, '技术部', 38, '2025-02-18 22:14:56', '2025-02-19 02:27:13');
INSERT INTO `departments` VALUES (3, '市场部', 39, '2025-02-18 22:14:56', '2025-02-19 02:27:13');
INSERT INTO `departments` VALUES (4, '财务部', 50, '2025-02-18 22:14:56', '2025-02-19 23:51:21');
INSERT INTO `departments` VALUES (9, '开发部', 48, '2025-02-19 01:31:49', '2025-02-19 23:04:14');

-- ----------------------------
-- Table structure for leave_approvals
-- ----------------------------
DROP TABLE IF EXISTS `leave_approvals`;
CREATE TABLE `leave_approvals`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `leave_id` int(11) NOT NULL COMMENT '审批人ID',
  `approver_id` int(11) NOT NULL COMMENT '审批人ID',
  `status` int(11) NOT NULL COMMENT '1:通过,2:拒绝',
  `comment` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `leave_id`(`leave_id`) USING BTREE,
  INDEX `approver_id`(`approver_id`) USING BTREE,
  CONSTRAINT `leave_approvals_ibfk_1` FOREIGN KEY (`leave_id`) REFERENCES `leaves` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `leave_approvals_ibfk_2` FOREIGN KEY (`approver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leave_approvals
-- ----------------------------
INSERT INTO `leave_approvals` VALUES (1, 5, 36, 1, '', '2025-02-19 21:27:50');
INSERT INTO `leave_approvals` VALUES (2, 5, 38, 1, '', '2025-02-19 21:28:29');
INSERT INTO `leave_approvals` VALUES (3, 3, 38, 1, '', '2025-02-19 21:28:33');
INSERT INTO `leave_approvals` VALUES (4, 2, 38, 1, '', '2025-02-19 21:28:35');
INSERT INTO `leave_approvals` VALUES (5, 1, 38, 1, '', '2025-02-19 21:28:37');
INSERT INTO `leave_approvals` VALUES (6, 6, 36, 1, '通过', '2025-02-19 21:34:57');
INSERT INTO `leave_approvals` VALUES (7, 7, 36, 1, 'aa', '2025-02-19 21:39:51');
INSERT INTO `leave_approvals` VALUES (8, 8, 36, 1, '同意', '2025-02-19 21:58:48');

-- ----------------------------
-- Table structure for leaves
-- ----------------------------
DROP TABLE IF EXISTS `leaves`;
CREATE TABLE `leaves`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `applicant_id` int(11) NOT NULL COMMENT '申请人ID',
  `type` int(11) NOT NULL COMMENT '1:事假,2:病假,3:年假,4:调休',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请假原因',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '0:待审批,1:审批中,2:已通过,3:已拒绝',
  `current_approver_id` int(11) NULL DEFAULT NULL COMMENT '当前审批人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `applicant_id`(`applicant_id`) USING BTREE,
  INDEX `current_approver_id`(`current_approver_id`) USING BTREE,
  CONSTRAINT `leaves_ibfk_1` FOREIGN KEY (`applicant_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `leaves_ibfk_2` FOREIGN KEY (`current_approver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leaves
-- ----------------------------
INSERT INTO `leaves` VALUES (1, 39, 1, '2025-02-18 16:00:00', '2025-02-19 16:00:00', 'asdasd', 2, NULL, '2025-02-19 19:40:18', '2025-02-19 21:28:37');
INSERT INTO `leaves` VALUES (2, 39, 1, '2025-02-18 16:00:00', '2025-02-19 16:00:00', 'asdasdasdasd', 2, NULL, '2025-02-19 20:03:07', '2025-02-19 21:28:35');
INSERT INTO `leaves` VALUES (3, 38, 1, '2025-02-18 16:00:00', '2025-02-19 16:00:00', 'asdasdasd', 2, NULL, '2025-02-19 20:23:25', '2025-02-19 21:28:33');
INSERT INTO `leaves` VALUES (5, 38, 1, '2025-02-18 16:00:00', '2025-02-19 16:00:00', '1233333', 2, NULL, '2025-02-19 21:00:57', '2025-02-19 21:28:29');
INSERT INTO `leaves` VALUES (6, 38, 1, '2025-02-18 16:00:00', '2025-02-19 16:00:00', '哈哈哈', 1, 38, '2025-02-19 21:34:31', '2025-02-19 21:34:56');
INSERT INTO `leaves` VALUES (7, 38, 2, '2025-02-18 16:00:00', '2025-02-19 16:00:00', 'asdasdasd', 2, NULL, '2025-02-19 21:39:30', '2025-02-19 21:39:51');
INSERT INTO `leaves` VALUES (8, 38, 1, '2025-02-19 16:00:00', '2025-02-21 16:00:00', '家里有事', 2, NULL, '2025-02-19 21:58:12', '2025-02-19 21:58:48');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sender_id` int(11) NULL DEFAULT NULL COMMENT '发送人ID',
  `receiver_id` int(11) NULL DEFAULT NULL COMMENT '接收人ID',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '接收部门ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_messages_sender`(`sender_id`) USING BTREE,
  INDEX `idx_messages_receiver`(`receiver_id`) USING BTREE,
  INDEX `idx_messages_dept`(`dept_id`) USING BTREE,
  INDEX `idx_messages_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`dept_id`) REFERENCES `departments` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (5, 36, NULL, NULL, '系统维护通知', '系统将于本周六晚上进行例行维护，请提前做好相关工作安排。', 0, '2025-02-19 02:27:13');
INSERT INTO `messages` VALUES (6, 38, NULL, 1, '技术部周会', '请各位同事准时参加周一上午10点的部门周会。', 0, '2025-02-19 02:27:13');
INSERT INTO `messages` VALUES (7, 38, 41, NULL, '代码审查', '请对用户管理模块的代码进行审查。', 1, '2025-02-19 02:27:13');
INSERT INTO `messages` VALUES (8, 39, 44, NULL, '报告提交', '请尽快提交市场调研报告。', 1, '2025-02-19 02:27:13');
INSERT INTO `messages` VALUES (9, 40, 46, NULL, '培训材料', '请准备下周的新员工培训材料。', 1, '2025-02-19 02:27:13');
INSERT INTO `messages` VALUES (10, 36, 37, NULL, '123', '123', 0, '2025-02-19 02:51:34');
INSERT INTO `messages` VALUES (11, 37, 38, NULL, 'asdasd', 'asdasd', 1, '2025-02-19 10:51:55');
INSERT INTO `messages` VALUES (12, 36, 49, NULL, '核对工资', '查看工资发放是否正确', 1, '2025-02-19 21:52:59');

-- ----------------------------
-- Table structure for tasks
-- ----------------------------
DROP TABLE IF EXISTS `tasks`;
CREATE TABLE `tasks`  (
  `task_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务内容',
  `creator_id` int(11) NULL DEFAULT NULL COMMENT '创建人ID',
  `assignee_id` int(11) NULL DEFAULT NULL COMMENT '执行人ID',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '所属部门ID',
  `status` int(11) NULL DEFAULT 0 COMMENT '任务状态:0待处理,1进行中,2已完成\"',
  `deadline` date NULL DEFAULT NULL COMMENT '截止日期',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `due_date` date NULL DEFAULT NULL COMMENT '截止时间',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_tasks_creator`(`creator_id`) USING BTREE,
  INDEX `idx_tasks_assignee`(`assignee_id`) USING BTREE,
  INDEX `idx_tasks_dept`(`dept_id`) USING BTREE,
  INDEX `idx_tasks_status`(`status`) USING BTREE,
  INDEX `idx_tasks_due_date`(`due_date`) USING BTREE,
  CONSTRAINT `tasks_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tasks_ibfk_2` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `tasks_ibfk_3` FOREIGN KEY (`dept_id`) REFERENCES `departments` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tasks
-- ----------------------------
INSERT INTO `tasks` VALUES (14, '系统升级计划', '制定下一季度系统升级计划', 38, 41, 1, 1, NULL, '2025-02-19 02:27:13', '2025-02-19 02:32:06', '2025-02-26');
INSERT INTO `tasks` VALUES (15, '代码重构', '重构用户管理模块代码', 38, 42, 1, 1, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-02-24');
INSERT INTO `tasks` VALUES (16, '性能优化', '优化系统响应速度', 38, 43, 1, 2, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-02-22');
INSERT INTO `tasks` VALUES (17, '市场调研', '进行竞品分析和市场调研', 39, 44, 2, 0, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-03-01');
INSERT INTO `tasks` VALUES (18, '营销方案', '制定Q2营销推广方案', 39, 45, 2, 1, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-02-25');
INSERT INTO `tasks` VALUES (19, '员工培训', '组织新员工入职培训', 40, 46, 3, 0, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-02-21');
INSERT INTO `tasks` VALUES (20, '绩效考核', '进行月度绩效考核', 40, 47, 3, 2, NULL, '2025-02-19 02:27:13', '2025-02-19 02:27:13', '2025-02-20');
INSERT INTO `tasks` VALUES (21, '数据库优化', '优化数据库查询性能', 38, 41, 1, 2, NULL, '2025-01-09 00:00:00', '2025-01-19 02:37:13', '2025-01-22');
INSERT INTO `tasks` VALUES (22, '接口开发', '开发新的API接口', 38, 42, 1, 2, NULL, '2025-02-12 00:00:00', '2025-02-19 02:27:13', '2025-02-16');
INSERT INTO `tasks` VALUES (23, '市场报告', '编写市场分析报告', 39, 44, 2, 2, NULL, '2025-02-14 00:00:00', '2025-02-19 02:27:13', '2025-02-17');
INSERT INTO `tasks` VALUES (24, 'asdasd', 'asdasd', 37, 38, NULL, 2, NULL, '2025-02-19 10:52:04', '2025-02-19 10:52:46', '2025-03-03');
INSERT INTO `tasks` VALUES (25, '123', '123', 37, 38, NULL, 2, NULL, '2025-02-19 11:08:08', '2025-02-19 21:57:08', '2025-02-19');
INSERT INTO `tasks` VALUES (26, '核对工资', '核对工资', 36, 49, NULL, 2, NULL, '2025-02-19 21:54:26', '2025-02-19 21:55:05', '2025-02-18');

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `role_id` int(11) NOT NULL COMMENT '主键',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES (1, '系统管理员', 'ADMIN', NULL);
INSERT INTO `user_roles` VALUES (2, '总经理', 'MANAGER', NULL);
INSERT INTO `user_roles` VALUES (3, '部门经理', 'DEPT_MANAGER', NULL);
INSERT INTO `user_roles` VALUES (4, '普通员工', 'EMPLOYEE', NULL);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '唯一用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role_id` int(11) NULL DEFAULT NULL COMMENT '角色ID',
  `dept_id` int(11) NULL DEFAULT NULL COMMENT '部门ID',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '人员状态',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_users_username`(`username`) USING BTREE,
  INDEX `idx_users_dept_id`(`dept_id`) USING BTREE,
  INDEX `idx_users_role_id`(`role_id`) USING BTREE,
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `user_roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `users_ibfk_2` FOREIGN KEY (`dept_id`) REFERENCES `departments` (`dept_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (36, 'admin', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '系统管理员', NULL, 1, NULL, 'admin@company.com', '13800000001', 1, '2025-02-19 02:27:13', '2025-02-19 18:58:33');
INSERT INTO `users` VALUES (37, 'zhangsan', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '张三', NULL, 2, NULL, 'zhangsan@company.com', '13800000002', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (38, 'lisi', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '李四', NULL, 3, 2, 'lisi@company.com', '13800000003', 1, '2025-02-19 02:27:13', '2025-02-19 20:23:06');
INSERT INTO `users` VALUES (39, 'wangwu', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '王五', NULL, 3, 2, 'wangwu@company.com', '13800000004', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (40, 'zhaoliu', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '赵六', NULL, 3, 3, 'zhaoliu@company.com', '13800000005', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (41, 'tech1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术一', NULL, 4, 1, 'tech1@company.com', '13800000006', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (42, 'tech2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术二', NULL, 4, 1, 'tech2@company.com', '13800000007', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (43, 'tech3', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '技术三', NULL, 4, 1, 'tech3@company.com', '13800000008', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (44, 'market1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '市场一', NULL, 4, 2, 'market1@company.com', '13800000009', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (45, 'market2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '市场二', NULL, 4, 2, 'market2@company.com', '13800000010', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (46, 'hr1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '人事一', NULL, 4, 3, 'hr1@company.com', '13800000011', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (47, 'hr2', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '人事二', NULL, 4, 3, 'hr2@company.com', '13800000012', 1, '2025-02-19 02:27:13', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (48, 'zzk', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '张泽坤', NULL, 3, 9, '1961359511@qq.com', '15649120590', 1, '2025-02-19 10:37:38', '2025-02-19 19:03:54');
INSERT INTO `users` VALUES (49, 'zhaosi', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '赵四', NULL, 4, 9, 'zhaosi@company.com', '15467435678', 1, '2025-02-19 21:46:52', '2025-02-19 21:46:52');
INSERT INTO `users` VALUES (50, 'caiwu1', '$2a$10$6c1rUqXfy7.kqd3v/X0zOOYz6TtxMQJqhN.vOCn5JgXyZVJDBNYwa', '魑魅', NULL, 3, 4, '191231232@qq.com', '12312312311', 1, '2025-02-19 23:51:13', '2025-02-19 23:51:13');

-- ----------------------------
-- View structure for department_stats
-- ----------------------------
DROP VIEW IF EXISTS `department_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `department_stats` AS select `d`.`dept_id` AS `dept_id`,`d`.`dept_name` AS `dept_name`,count(`u`.`user_id`) AS `user_count`,`u`.`name` AS `manager_name`,count(`t`.`task_id`) AS `task_count`,sum((case when (`t`.`status` = 2) then 1 else 0 end)) AS `completed_tasks` from ((`departments` `d` left join `users` `u` on((`d`.`manager_id` = `u`.`user_id`))) left join `tasks` `t` on((`d`.`dept_id` = `t`.`dept_id`))) group by `d`.`dept_id`;

-- ----------------------------
-- View structure for user_details
-- ----------------------------
DROP VIEW IF EXISTS `user_details`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `user_details` AS select `u`.`user_id` AS `user_id`,`u`.`username` AS `username`,`u`.`password` AS `password`,`u`.`name` AS `name`,`u`.`real_name` AS `real_name`,`u`.`role_id` AS `role_id`,`u`.`dept_id` AS `dept_id`,`u`.`email` AS `email`,`u`.`phone` AS `phone`,`u`.`status` AS `status`,`u`.`create_time` AS `create_time`,`u`.`update_time` AS `update_time`,`d`.`dept_name` AS `dept_name`,`r`.`role_name` AS `role_name`,`r`.`role_code` AS `role_code`,`dm`.`name` AS `manager_name` from (((`users` `u` left join `departments` `d` on((`u`.`dept_id` = `d`.`dept_id`))) left join `user_roles` `r` on((`u`.`role_id` = `r`.`role_id`))) left join `users` `dm` on((`d`.`manager_id` = `dm`.`user_id`)));

-- ----------------------------
-- Procedure structure for calculate_annual_bonus
-- ----------------------------
DROP PROCEDURE IF EXISTS `calculate_annual_bonus`;
delimiter ;;
CREATE PROCEDURE `calculate_annual_bonus`(IN bonus_year INT)
BEGIN
    -- 删除已存在的该年年终奖记录
    DELETE FROM annual_bonus WHERE year = bonus_year;
    
    -- 插入新的年终奖记录
    INSERT INTO annual_bonus (
        emp_id,
        year,
        total_salary,
        total_allowance,
        bonus_amount
    )
    SELECT 
        e.emp_id,
        bonus_year,
        -- 计算年度工资总额
        (
            SELECT SUM(net_salary)
            FROM monthly_salary ms
            WHERE ms.emp_id = e.emp_id
            AND YEAR(ms.salary_month) = bonus_year
        ) as total_salary,
        -- 计算年度津贴总额（加班费总和）
        (
            SELECT SUM(overtime_pay)
            FROM monthly_salary ms
            WHERE ms.emp_id = e.emp_id
            AND YEAR(ms.salary_month) = bonus_year
        ) as total_allowance,
        -- 计算年终奖金 = (年度工资总额 + 年度津贴总额) * 系数
        -- 系数根据工龄确定：
        -- 1年以下：0.5
        -- 1-3年：1
        -- 3-5年：1.5
        -- 5年以上：2
        (
            SELECT 
                (SUM(ms.net_salary) + SUM(ms.overtime_pay)) * 
                CASE 
                    WHEN TIMESTAMPDIFF(YEAR, e.hire_date, CONCAT(bonus_year, '-12-31')) < 1 THEN 0.5
                    WHEN TIMESTAMPDIFF(YEAR, e.hire_date, CONCAT(bonus_year, '-12-31')) < 3 THEN 1
                    WHEN TIMESTAMPDIFF(YEAR, e.hire_date, CONCAT(bonus_year, '-12-31')) < 5 THEN 1.5
                    ELSE 2
                END
            FROM monthly_salary ms
            WHERE ms.emp_id = e.emp_id
            AND YEAR(ms.salary_month) = bonus_year
        ) as bonus_amount
    FROM employees e
    WHERE e.status = 'active';
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for calculate_monthly_salary
-- ----------------------------
DROP PROCEDURE IF EXISTS `calculate_monthly_salary`;
delimiter ;;
CREATE PROCEDURE `calculate_monthly_salary`(IN salary_month DATE)
BEGIN
    -- 删除已存在的该月工资记录
    DELETE FROM monthly_salary 
    WHERE DATE_FORMAT(salary_month, '%Y-%m') = DATE_FORMAT(p_salary_month, '%Y-%m');
    
    -- 插入新的工资记录
    INSERT INTO monthly_salary (
        emp_id, 
        salary_month, 
        base_salary,
        overtime_pay,
        deductions,
        net_salary
    )
    SELECT 
        e.emp_id,
        p_salary_month,
        p.base_salary,
        -- 计算加班费
        COALESCE(
            (
                SELECT SUM(
                    CASE 
                        WHEN ot_type = 'workday' THEN ot_hours * (p.base_salary / 21.75 / 8 * 1.5)
                        WHEN ot_type = 'weekend' THEN ot_hours * (p.base_salary / 21.75 / 8 * 2)
                        WHEN ot_type = 'holiday' THEN ot_hours * (p.base_salary / 21.75 / 8 * 3)
                    END
                )
                FROM overtime_records o
                WHERE o.emp_id = e.emp_id 
                AND DATE_FORMAT(o.ot_date, '%Y-%m') = DATE_FORMAT(p_salary_month, '%Y-%m')
                AND o.status = 'approved'
            ), 
            0
        ) as overtime_pay,
        -- 计算考勤扣除
        COALESCE(
            (
                SELECT SUM(
                    CASE 
                        WHEN status = 'absent' THEN p.base_salary / 21.75
                        WHEN status = 'late' THEN p.base_salary / 21.75 * 0.5
                        WHEN status = 'leave' THEN p.base_salary / 21.75 * 0.3
                        ELSE 0
                    END
                )
                FROM attendance_records a
                WHERE a.emp_id = e.emp_id 
                AND DATE_FORMAT(a.work_date, '%Y-%m') = DATE_FORMAT(p_salary_month, '%Y-%m')
            ),
            0
        ) as deductions,
        -- 计算实发工资 = 基本工资 + 加班费 - 考勤扣除
        p.base_salary + 
        COALESCE(
            (
                SELECT SUM(
                    CASE 
                        WHEN ot_type = 'workday' THEN ot_hours * (p.base_salary / 21.75 / 8 * 1.5)
                        WHEN ot_type = 'weekend' THEN ot_hours * (p.base_salary / 21.75 / 8 * 2)
                        WHEN ot_type = 'holiday' THEN ot_hours * (p.base_salary / 21.75 / 8 * 3)
                    END
                )
                FROM overtime_records o
                WHERE o.emp_id = e.emp_id 
                AND DATE_FORMAT(o.ot_date, '%Y-%m') = DATE_FORMAT(p_salary_month, '%Y-%m')
                AND o.status = 'approved'
            ),
            0
        ) -
        COALESCE(
            (
                SELECT SUM(
                    CASE 
                        WHEN status = 'absent' THEN p.base_salary / 21.75
                        WHEN status = 'late' THEN p.base_salary / 21.75 * 0.5
                        WHEN status = 'leave' THEN p.base_salary / 21.75 * 0.3
                        ELSE 0
                    END
                )
                FROM attendance_records a
                WHERE a.emp_id = e.emp_id 
                AND DATE_FORMAT(a.work_date, '%Y-%m') = DATE_FORMAT(p_salary_month, '%Y-%m')
            ),
            0
        ) as net_salary
    FROM employees e
    JOIN job_positions p ON e.position_id = p.position_id
    WHERE e.status = 'active';
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for register_user
-- ----------------------------
DROP PROCEDURE IF EXISTS `register_user`;
delimiter ;;
CREATE PROCEDURE `register_user`(IN p_username VARCHAR(50),
    IN p_password VARCHAR(100),
    IN p_name VARCHAR(50),
    IN p_role_id INT,
    IN p_dept_id INT,
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    OUT p_user_id INT)
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table departments
-- ----------------------------
DROP TRIGGER IF EXISTS `before_department_manager_update`;
delimiter ;;
CREATE TRIGGER `before_department_manager_update` BEFORE UPDATE ON `departments` FOR EACH ROW BEGIN
    -- 检查新经理是否为部门经理角色
    IF NEW.manager_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM users 
        WHERE user_id = NEW.manager_id 
        AND role_id = 3
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Manager must have department manager role';
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table leave_approvals
-- ----------------------------
DROP TRIGGER IF EXISTS `after_leave_approval`;
delimiter ;;
CREATE TRIGGER `after_leave_approval` AFTER INSERT ON `leave_approvals` FOR EACH ROW BEGIN
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table users
-- ----------------------------
DROP TRIGGER IF EXISTS `before_user_delete`;
delimiter ;;
CREATE TRIGGER `before_user_delete` BEFORE DELETE ON `users` FOR EACH ROW BEGIN
    -- 检查是否为部门经理
    IF EXISTS (
        SELECT 1 FROM departments 
        WHERE manager_id = OLD.user_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete user who is a department manager';
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
