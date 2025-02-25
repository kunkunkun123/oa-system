# 项目说明文档

## 项目概述
本项目是一个基于Spring Boot和Vue.js的办公自动化（OA）系统，旨在提高企业内部的管理效率。系统包括用户管理、部门管理、请假管理、任务管理、消息通知和公告管理等功能。

## 前端部分
### 技术栈
- **Vue.js**：用于构建用户界面，提供响应式的数据绑定和组件化开发。
- **Vue Router**：用于实现前端路由，支持单页面应用的导航。
- **Vuex**：用于状态管理，集中管理应用的状态，方便组件之间的状态共享。
- **Axios**：用于与后端API进行HTTP请求，处理数据交互。

### 主要功能模块
1. **登录与注册**
   - **`Login.vue`**：用户登录页面，包含表单验证，确保用户输入的用户名和密码有效。登录成功后，用户信息和权限会存储在Vuex状态管理中。
   - **`Register.vue`**：用户注册页面，允许新用户创建账户，包含输入验证和错误提示。

2. **用户管理**
   - **`Users.vue`**：展示用户列表，支持增删改查操作。用户可以通过表格查看所有用户信息，点击某个用户可以进入用户详情页面进行编辑。
   - **用户详情页面**：展示用户的详细信息，包括角色、部门等，支持修改用户信息和重置密码。

3. **部门管理**
   - **`Departments.vue`**：管理部门信息，支持部门的创建、修改和删除。用户可以查看部门列表，点击某个部门可以查看其详细信息。
   - **部门详情页面**：展示部门的详细信息，包括部门经理、成员等，支持修改部门信息。

4. **公告管理**
   - **`Announcements.vue`**：管理公告信息，支持公告的发布、编辑和删除。用户可以查看公告列表，点击某个公告可以查看其详细内容。
   - **公告详情页面**：展示公告的详细信息，支持编辑和删除操作。

5. **任务管理**
   - **`Tasks.vue`**：管理任务信息，支持任务的创建、分配和状态更新。用户可以查看任务列表，点击某个任务可以查看其详细信息。
   - **任务详情页面**：展示任务的详细信息，包括任务状态、截止日期等，支持修改任务信息和更新状态。

6. **消息管理**
   - **`Messages.vue`**：展示消息列表，用户可以查看所有消息，点击某条消息可以查看详细内容。
   - **`Message.vue`**：展示单条消息的详细信息，支持回复和删除操作。

7. **仪表盘**
   - **`Dashboard.vue`**：系统的总览页面，展示关键统计信息，如用户数量、任务进度、公告数量等。仪表盘通过图表和卡片的形式展示数据，提供直观的视图。

### 关键文件和结构
- **`src/main/resources/static/frontend/src`**：前端源代码目录，包含所有Vue组件和相关资源。
- **`src/main/resources/static/frontend/src/views`**：包含各个页面组件的目录，按功能模块组织。
- **`src/main/resources/static/frontend/src/store`**：Vuex状态管理目录，包含状态、变更和动作的定义。
- **`src/main/resources/static/frontend/src/router`**：路由配置文件，定义了各个页面的路由规则。
- **`src/main/resources/static/frontend/src/api`**：API请求封装，集中管理与后端的HTTP请求。

### 组件功能和交互逻辑
- **`Login.vue`**：
  - 用户输入用户名和密码，进行表单验证。
  - 登录成功后，调用后端API，获取用户信息和权限。
  - 使用Vuex存储用户信息。

- **`Register.vue`**：
  - 用户输入注册信息，进行表单验证。
  - 调用后端API进行用户注册，处理注册结果。

- **`Users.vue`**：
  - 显示用户列表，支持分页和搜索功能。
  - 点击用户进入用户详情页面，支持编辑和删除操作。

- **`Departments.vue`**：
  - 显示部门列表，支持创建、修改和删除部门。
  - 点击部门进入部门详情页面，展示部门成员和任务。

- **`Announcements.vue`**：
  - 显示公告列表，支持发布、编辑和删除公告。
  - 点击公告进入公告详情页面，展示公告内容。

- **`Tasks.vue`**：
  - 显示任务列表，支持创建、分配和更新任务状态。
  - 点击任务进入任务详情页面，展示任务信息。

- **`Messages.vue`**：
  - 显示消息列表，支持查看和回复消息。
  - 点击消息进入消息详情页面，展示消息内容。

- **`Dashboard.vue`**：
  - 显示系统的关键统计信息，使用图表展示数据。

### API接口文档
- **用户登录**
  - **请求**：
    - URL: `/api/login`
    - 方法: `POST`
    - 参数: `{ username, password }`
  - **响应**：
    - 成功: `{ token, userInfo }`
    - 失败: `{ error }`

- **用户注册**
  - **请求**：
    - URL: `/api/register`
    - 方法: `POST`
    - 参数: `{ username, password, name, email, phone }`
  - **响应**：
    - 成功: `{ message: '注册成功' }`
    - 失败: `{ error }`

## 后端部分
### 技术栈
- **Spring Boot**：用于构建后端服务，提供快速开发和部署的能力。
- **Spring Security**：用于安全认证，保护API接口，确保只有授权用户才能访问。
- **JPA/Hibernate**：用于数据持久化，简化数据库操作，提供对象关系映射。
- **MySQL**：作为数据库，存储系统的所有数据。

### 主要功能模块
1. **用户和权限管理**
   - 提供用户注册、登录、角色管理等功能。
   - **SQL示例**：用户注册时插入用户信息的SQL语句：
     ```sql
     INSERT INTO users (username, password, name, role_id, email, phone, create_time)
     VALUES (?, ?, ?, ?, ?, ?, NOW());
     ```

2. **组织架构管理**
   - 管理部门和员工信息。
   - **SQL示例**：创建部门的SQL语句：
     ```sql
     INSERT INTO departments (dept_name, manager_id, create_time)
     VALUES (?, ?, NOW());
     ```

3. **请假管理系统**
   - 提供请假申请和审批功能。
   - **SQL示例**：插入请假申请的SQL语句：
     ```sql
     INSERT INTO leaves (applicant_id, type, start_time, end_time, reason, status, create_time)
     VALUES (?, ?, ?, ?, ?, 0, NOW());
     ```

4. **任务管理系统**
   - 管理任务的创建、分配和跟踪。
   - **SQL示例**：更新任务状态的SQL语句：
     ```sql
     UPDATE tasks SET status = ?, update_time = NOW() WHERE task_id = ?;
     ```

5. **消息通知系统**
   - 提供站内消息和系统通知功能。
   - **SQL示例**：插入消息的SQL语句：
     ```sql
     INSERT INTO messages (sender_id, receiver_id, title, content, create_time)
     VALUES (?, ?, ?, ?, NOW());
     ```

6. **公告管理**
   - 管理公告的发布和查看。
   - **SQL示例**：发布公告的SQL语句：
     ```sql
     INSERT INTO announcements (title, content, creator_id, create_time)
     VALUES (?, ?, ?, NOW());
     ```

### 关键文件和结构
- **`src/main/java/com/office/controller`**：控制器目录，包含各个功能模块的控制器，处理HTTP请求并返回响应。
- **`src/main/java/com/office/service`**：服务层目录，包含业务逻辑，调用数据访问层的方法。
- **`src/main/java/com/office/repository`**：数据访问层目录，包含JPA接口，负责与数据库的交互。

### 功能模块实现逻辑
- **用户和权限管理**：
  - 控制器处理用户注册和登录请求，调用服务层进行业务逻辑处理。
  - 服务层调用数据访问层进行数据库操作。

- **组织架构管理**：
  - 控制器处理部门的创建、修改和删除请求，调用服务层进行业务逻辑处理。
  - 服务层调用数据访问层进行数据库操作。

### 后端API接口文档
- **用户注册**
  - **请求**：
    - URL: `/api/users/register`
    - 方法: `POST`
    - 参数: `{ username, password, name, role_id, email, phone }`
  - **响应**：
    - 成功: `{ message: '用户注册成功' }`
    - 失败: `{ error }`

- **获取用户列表**
  - **请求**：
    - URL: `/api/users`
    - 方法: `GET`
  - **响应**：
    - 成功: `{ users: [...] }`
    - 失败: `{ error }`

## 数据库部分
### 数据库设计
数据库名为`oa_system`，主要用于支持OA系统的功能。

### 主要表结构
1. **用户表 (users)**：存储用户信息，包括用户名、密码、角色ID、部门ID等。
   - **字段**：
     - `user_id` INT PRIMARY KEY AUTO_INCREMENT
     - `username` VARCHAR(50) UNIQUE
     - `password` VARCHAR(100)
     - `name` VARCHAR(50)
     - `role_id` INT
     - `dept_id` INT
     - `email` VARCHAR(100)
     - `phone` VARCHAR(20)
     - `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

2. **部门表 (departments)**：存储部门信息，包括部门名称、经理ID等。
   - **字段**：
     - `dept_id` INT PRIMARY KEY AUTO_INCREMENT
     - `dept_name` VARCHAR(100) UNIQUE
     - `manager_id` INT
     - `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

3. **用户角色表 (user_roles)**：存储角色信息，包括角色名称和角色编码。
   - **字段**：
     - `role_id` INT PRIMARY KEY AUTO_INCREMENT
     - `role_name` VARCHAR(50)
     - `role_code` VARCHAR(50)

4. **任务表 (tasks)**：存储任务信息，包括任务标题、内容、创建者ID、执行人ID等。
   - **字段**：
     - `task_id` INT PRIMARY KEY AUTO_INCREMENT
     - `title` VARCHAR(200)
     - `content` TEXT
     - `creator_id` INT
     - `assignee_id` INT
     - `dept_id` INT
     - `status` INT DEFAULT 0
     - `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

5. **消息表 (messages)**：存储消息信息，包括发送者ID、接收者ID、消息内容等。
   - **字段**：
     - `id` INT PRIMARY KEY AUTO_INCREMENT
     - `sender_id` INT
     - `receiver_id` INT
     - `title` VARCHAR(200)
     - `content` TEXT
     - `is_read` BOOLEAN DEFAULT FALSE
     - `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP

6. **公告表 (announcements)**：存储公告信息，包括公告标题、内容、创建者ID等。
   - **字段**：
     - `announcement_id` INT PRIMARY KEY AUTO_INCREMENT
     - `title` VARCHAR(100)
     - `content` TEXT
     - `creator_id` INT
     - `create_time` TIMESTAMP NOT NULL

7. **请假表 (leaves)**：存储请假申请信息，包括申请人ID、请假类型、开始时间、结束时间等。
   - **字段**：
     - `id` INT PRIMARY KEY AUTO_INCREMENT
     - `applicant_id` INT
     - `type` INT
     - `start_time` DATETIME
     - `end_time` DATETIME
     - `reason` VARCHAR(500)
     - `status` INT DEFAULT 0
     - `create_time` DATETIME

### 触发器、存储过程和视图
- **触发器**：用于自动化业务规则的执行，如在更新部门经理前检查角色。
  - **示例**：`before_department_manager_update`触发器，确保新经理必须是部门经理角色。
- **存储过程**：封装复杂的业务逻辑，如计算年终奖金和月工资。
  - **示例**：`calculate_annual_bonus`存储过程，计算年度奖金。
- **视图**：简化数据查询，如部门统计视图和用户详情视图。
  - **示例**：`department_stats`视图，统计各部门的用户数量和任务数量。

### 索引设计
通过合理的索引设计优化了查询性能，确保系统在高并发情况下的响应速度。主要索引包括：
- 用户表的`username`唯一索引
- 任务表的`creator_id`和`assignee_id`索引
- 消息表的`sender_id`和`receiver_id`索引

### 函数
- **示例**：`register_user`存储过程，处理用户注册逻辑，确保用户名唯一，插入新用户记录。

### 外键关系和约束条件
- **用户表**：
  - `role_id`外键关联到`user_roles`表，确保用户角色有效。
  - `dept_id`外键关联到`departments`表，确保用户部门有效。

- **部门表**：
  - `manager_id`外键关联到`users`表，确保部门经理有效。

- **任务表**：
  - `creator_id`和`assignee_id`外键关联到`users`表，确保任务的创建者和执行者有效。
  - `dept_id`外键关联到`departments`表，确保任务所属部门有效。

### 数据库备份和恢复策略
- 定期使用`mysqldump`工具进行数据库备份。
- 备份文件存储在安全的云存储中，确保数据安全。
- 恢复时使用`mysql`命令导入备份文件。

### 数据库性能优化策略
- 使用索引加速查询，特别是在高频查询的字段上。
- 定期分析查询性能，使用`EXPLAIN`语句优化慢查询。
- 监控数据库性能指标，及时调整数据库配置。

## 系统架构
### 整体架构图
```
+----------------+     +----------------+     +----------------+
|   前端层       |     |   应用层       |     |   数据层       |
|  Vue.js + UI   | --> | Spring Boot   | --> |    MySQL      |
+----------------+     +----------------+     +----------------+
```

### 业务流程图
### 请假申请流程
```
员工提交请假申请 -> 部门经理审批 -> [总经理审批] -> 人事备案 -> 结束
```

### 任务管理流程
```
任务创建 -> 任务分配 -> 任务执行 -> 任务状态更新 -> 任务完成
```

### 用户管理流程
```
用户注册 -> 用户信息审核 -> 用户角色分配 -> 用户启用
```

## 数据库ER图
### ER图示例
```
+----------------+     +----------------+     +----------------+
|    users       |     |   departments   |     |   user_roles   |
|----------------|     |----------------|     |----------------|
| user_id (PK)  |     | dept_id (PK)   |     | role_id (PK)   |
| username       |     | dept_name      |     | role_name      |
| password       |     | manager_id     |     | role_code      |
| name           |     | create_time    |     |                |
| role_id (FK)  |     +----------------+     +----------------+
| dept_id (FK)  |
| email          |
| phone          |
| create_time    |
+----------------+

+----------------+     +----------------+
|    tasks       |     |   messages      |
|----------------|     |----------------|
| task_id (PK)  |     | id (PK)        |
| title          |     | sender_id (FK) |
| content        |     | receiver_id (FK)|
| creator_id (FK)|     | title          |
| assignee_id (FK)|    | content        |
| dept_id (FK)  |     | is_read        |
| status         |     | create_time    |
| create_time    |     +----------------+
+----------------+

+----------------+     +----------------+
| announcements   |     |    leaves      |
|----------------|     |----------------|
| announcement_id (PK)| | id (PK)        |
| title          |     | applicant_id (FK)|
| content        |     | type           |
| creator_id (FK)|     | start_time     |
| create_time    |     | end_time       |
+----------------+     | reason         |
                      | status         |
                      | create_time    |
                      +----------------+
```

### 后端补充
#### 复杂查询示例
1. **获取部门任务统计**
```sql
SELECT 
    d.dept_name,
    COUNT(t.task_id) as total_tasks,
    SUM(CASE WHEN t.status = 0 THEN 1 ELSE 0 END) as pending_tasks,
    SUM(CASE WHEN t.status = 1 THEN 1 ELSE 0 END) as in_progress_tasks,
    SUM(CASE WHEN t.status = 2 THEN 1 ELSE 0 END) as completed_tasks
FROM departments d
LEFT JOIN tasks t ON d.dept_id = t.dept_id
GROUP BY d.dept_id;
```

2. **获取用户请假统计**
```sql
SELECT 
    u.name,
    COUNT(l.id) as total_leaves,
    SUM(CASE WHEN l.type = 1 THEN 1 ELSE 0 END) as personal_leaves,
    SUM(CASE WHEN l.type = 2 THEN 1 ELSE 0 END) as sick_leaves,
    SUM(CASE WHEN l.type = 3 THEN 1 ELSE 0 END) as annual_leaves
FROM users u
LEFT JOIN leaves l ON u.user_id = l.applicant_id
WHERE YEAR(l.create_time) = YEAR(CURRENT_DATE)
GROUP BY u.user_id;
```

#### 事务处理示例
```java
@Transactional
public void processLeaveApplication(LeaveApplication application) {
    // 1. 插入请假记录
    Leave leave = leaveRepository.save(application.getLeave());
    
    // 2. 创建审批流程
    LeaveApproval approval = new LeaveApproval();
    approval.setLeaveId(leave.getId());
    approval.setApproverId(getCurrentApprover(application));
    approvalRepository.save(approval);
    
    // 3. 发送通知
    Message message = new Message();
    message.setReceiverId(approval.getApproverId());
    message.setTitle("新的请假申请");
    message.setContent("您有一个新的请假申请需要审批");
    messageRepository.save(message);
} 
```

## 测试部分
### 测试策略
- **单元测试**：使用JUnit和Mockito进行后端逻辑的单元测试。
- **集成测试**：使用Spring Boot Test进行API接口的集成测试，确保各个模块之间的协作正常。
- **前端测试**：使用Jest和Vue Test Utils进行前端组件的单元测试。

### 测试用例示例
- **用户注册测试**：
  - 测试有效注册流程。
  - 测试用户名重复的情况。
- **请假申请测试**：
  - 测试有效请假申请流程。
  - 测试请假申请的审批流程。

## 部署和运维
### 部署流程
- 使用Docker容器化应用，确保环境一致性。
- 使用Kubernetes进行容器编排，管理应用的扩展和负载均衡。
- 配置CI/CD流水线，实现自动化部署。

### 运维策略
- 监控系统性能，使用Prometheus和Grafana进行实时监控。
- 定期进行系统健康检查，确保服务的可用性。
- 记录日志，使用ELK栈进行日志管理和分析。