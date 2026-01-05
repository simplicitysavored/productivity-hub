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
-- 添加字段注释
COMMENT ON COLUMN users.id IS '用户ID';
COMMENT ON COLUMN users.nickname IS '用户昵称';
COMMENT ON COLUMN users.avatar_url IS '用户头像';
COMMENT ON COLUMN users.email IS '用户邮箱';
COMMENT ON COLUMN users.status IS '用户状态：1-正常, 0-禁用';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';

-- 2. 用户认证表：存放登录凭证，支持多方式登录
CREATE TABLE IF NOT EXISTS user_auths (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,                    -- 关联 users 表的 id
    identity_type VARCHAR(20) NOT NULL,         -- 认证类型：'local', 'github', 'google'
    identifier VARCHAR(100) NOT NULL,           -- 标识：本地存用户名，第三方存 OpenID
    credential VARCHAR(255),                    -- 凭证：本地存加密密码，第三方可为空
    verified BOOLEAN DEFAULT FALSE,             -- 是否已验证（如邮箱激活）
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(identity_type, identifier)           -- 同一平台下标识必须唯一
);
-- 字段注释
COMMENT ON COLUMN user_auths.id IS '用户认证ID';
COMMENT ON COLUMN user_auths.user_id IS '用户ID';
COMMENT ON COLUMN user_auths.identity_type IS '认证类型：local, github, google';
COMMENT ON COLUMN user_auths.identifier IS '标识：本地存用户名，第三方存 OpenID';
COMMENT ON COLUMN user_auths.credential IS '凭证：本地存加密密码，第三方可为空';
COMMENT ON COLUMN user_auths.verified IS '是否已验证（如邮箱激活）';
COMMENT ON COLUMN user_auths.created_at IS '创建时间';
COMMENT ON COLUMN user_auths.updated_at IS '更新时间';

-- 为常用查询添加索引
CREATE INDEX idx_user_auths_user_id ON user_auths(user_id);


-- 3、用户 Token 记录
CREATE TABLE user_tokens (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    jti VARCHAR(64) NOT NULL UNIQUE,  -- JWT 唯一标识
    token_val TEXT NOT NULL,          -- 完整的 Token (可选)
    client_ip VARCHAR(50),            -- 登录 IP
    expire_at TIMESTAMP WITH TIME ZONE NOT NULL,     -- 过期时间
    is_revoked BOOLEAN DEFAULT FALSE, -- 是否手动撤销
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_token_jti ON user_tokens(jti);
CREATE INDEX idx_token_user_id ON user_tokens(user_id); -- 建议增加，方便按用户清理或统计
