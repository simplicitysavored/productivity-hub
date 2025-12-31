-- 1. 用户基础表：存放用户非敏感信息
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,                      -- 唯一ID（建议使用雪花算法生成）
    nickname VARCHAR(50) NOT NULL,              -- 用户昵称
    avatar_url VARCHAR(255),                    -- 头像路径
    email VARCHAR(100) UNIQUE,                  -- 绑定邮箱
    status SMALLINT DEFAULT 1,                  -- 状态：1-正常, 0-禁用
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. 用户认证表：存放登录凭证，支持多方式登录
CREATE TABLE IF NOT EXISTS user_auths (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,                    -- 关联 users 表的 id
    identity_type VARCHAR(20) NOT NULL,         -- 认证类型：'local', 'github', 'google'
    identifier VARCHAR(100) NOT NULL,           -- 标识：本地存用户名，第三方存 OpenID
    credential VARCHAR(255),                    -- 凭证：本地存加密密码，第三方可为空
    verified BOOLEAN DEFAULT FALSE,             -- 是否已验证（如邮箱激活）

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(identity_type, identifier)           -- 同一平台下标识必须唯一
);

-- 为常用查询添加索引
CREATE INDEX idx_user_auths_user_id ON user_auths(user_id);