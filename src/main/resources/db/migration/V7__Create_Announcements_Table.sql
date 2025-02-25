CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    creator_id INT NOT NULL,
    create_time DATETIME NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已下线，1-已发布',
    FOREIGN KEY (creator_id) REFERENCES users(user_id)
); 