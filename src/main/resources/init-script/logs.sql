drop table if exists sys_login_log;
-- 登录日志表 (PostgreSQL)
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50) not null,
    ip_address VARCHAR(50),
    login_location VARCHAR(255),  -- 预留位置信息
    browser VARCHAR(100),         -- 浏览器信息
    os VARCHAR(100),              -- 操作系统
    status SMALLINT DEFAULT 1,    -- 状态：1-成功, 0-失败
    msg TEXT,                     -- 提示消息（如密码错误）
    login_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_login_log_uid ON sys_login_log(user_id);
CREATE INDEX idx_login_log_time ON sys_login_log(login_time);