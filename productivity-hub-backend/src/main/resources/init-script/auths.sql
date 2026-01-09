-- 1. 用户基础表：存放用户非敏感信息
drop table if exists users;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,                      -- 唯一ID（建议使用雪花算法生成）
    nickname VARCHAR(50) NOT NULL,              -- 用户昵称
    avatar_url VARCHAR(255),                    -- 头像路径
    email VARCHAR(100) UNIQUE,                  -- 绑定邮箱
    status SMALLINT DEFAULT 1,                  -- 状态：1-正常, 0-禁用
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE users IS '用户基础信息表（存放非敏感信息）';
-- 添加字段注释
COMMENT ON COLUMN users.id IS '用户ID';
COMMENT ON COLUMN users.nickname IS '用户昵称';
COMMENT ON COLUMN users.avatar_url IS '用户头像';
COMMENT ON COLUMN users.email IS '用户邮箱';
COMMENT ON COLUMN users.status IS '用户状态：1-正常, 0-禁用';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.created_by IS '创建人';
COMMENT ON COLUMN users.updated_at IS '更新时间';
COMMENT ON COLUMN users.updated_by IS '更新人';
-- 基础数据
insert into users(id, nickname, avatar_url, email, status) values(1, 'admin', null, null, 1);

-- 2. 用户认证表：存放登录凭证，支持多方式登录
drop table if exists user_auths;
CREATE TABLE IF NOT EXISTS user_auths (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,                    -- 关联 users 表的 id
    identity_type VARCHAR(20) NOT NULL,         -- 认证类型：'local', 'github', 'google'
    identifier VARCHAR(100) NOT NULL,           -- 标识：本地存用户名，第三方存 OpenID
    credential VARCHAR(255),                    -- 凭证：本地存加密密码，第三方可为空
    verified BOOLEAN DEFAULT FALSE,             -- 是否已验证（如邮箱激活）
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,

    UNIQUE(identity_type, identifier)           -- 同一平台下标识必须唯一
);
-- 表注释
COMMENT ON TABLE user_auths IS '用户认证信息表（支持多方式登录）';
-- 字段注释
COMMENT ON COLUMN user_auths.id IS '用户认证ID';
COMMENT ON COLUMN user_auths.user_id IS '用户ID';
COMMENT ON COLUMN user_auths.identity_type IS '认证类型：local, github, google';
COMMENT ON COLUMN user_auths.identifier IS '标识：本地存用户名，第三方存 OpenID';
COMMENT ON COLUMN user_auths.credential IS '凭证：本地存加密密码，第三方可为空';
COMMENT ON COLUMN user_auths.verified IS '是否已验证（如邮箱激活）';
COMMENT ON COLUMN user_auths.created_at IS '创建时间';
COMMENT ON COLUMN user_auths.created_by IS '创建人';
COMMENT ON COLUMN user_auths.updated_at IS '更新时间';
COMMENT ON COLUMN user_auths.updated_by IS '更新人';
-- 为常用查询添加索引
CREATE INDEX idx_user_auths_user_id ON user_auths(user_id);
-- 基础数据
insert into user_auths(id, user_id, identity_type, identifier, credential, verified) values(1, 1, 'local', 'admin', '$2a$10$iG8c0A0h/04ovFZK333Sm.i.wsuW8a8TNkzpLVNjsJzK6gF9k00um', false);


-- 3、用户 Token 记录
drop table if exists user_tokens;
CREATE TABLE IF NOT EXISTS user_tokens (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    jti VARCHAR(64) NOT NULL UNIQUE,  -- JWT 唯一标识
    token_val TEXT NOT NULL,          -- 完整的 Token (可选)
    client_ip VARCHAR(50),            -- 登录 IP
    expire_at TIMESTAMP WITH TIME ZONE NOT NULL,     -- 过期时间
    is_revoked BOOLEAN DEFAULT FALSE, -- 是否手动撤销
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE user_tokens IS '用户 Token 记录表';
-- 字段注释
COMMENT ON COLUMN user_tokens.id IS '主键';
COMMENT ON COLUMN user_tokens.user_id IS '用户ID';
COMMENT ON COLUMN user_tokens.jti IS 'JWT 唯一标识';
COMMENT ON COLUMN user_tokens.token_val IS '完整的 Token（可选）';
COMMENT ON COLUMN user_tokens.client_ip IS '登录 IP';
COMMENT ON COLUMN user_tokens.expire_at IS '过期时间';
COMMENT ON COLUMN user_tokens.is_revoked IS '是否手动撤销';
COMMENT ON COLUMN user_tokens.created_at IS '创建时间';
COMMENT ON COLUMN user_tokens.created_by IS '创建人';
COMMENT ON COLUMN user_tokens.updated_at IS '更新时间';
COMMENT ON COLUMN user_tokens.updated_by IS '更新人';

CREATE INDEX idx_token_jti ON user_tokens(jti);
CREATE INDEX idx_token_user_id ON user_tokens(user_id); -- 建议增加，方便按用户清理或统计

-- 角色表
drop table if exists roles;
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE roles IS '角色表';
-- 字段注释
COMMENT ON COLUMN roles.id IS '角色ID';
COMMENT ON COLUMN roles.role_name IS '角色名称';
COMMENT ON COLUMN roles.role_code IS '角色编码，如：ROLE_ADMIN、ROLE_USER';
COMMENT ON COLUMN roles.description IS '角色描述';
COMMENT ON COLUMN roles.created_at IS '创建时间';
COMMENT ON COLUMN roles.created_by IS '创建人';
COMMENT ON COLUMN roles.updated_at IS '更新时间';
COMMENT ON COLUMN roles.updated_by IS '更新人';
-- 索引
CREATE UNIQUE INDEX idx_role_code ON roles(role_code);
-- 基础数据
insert into roles(id, role_name, role_code, description) values(1, '管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限');
insert into roles(id, role_name, role_code, description) values(2, '普通用户', 'ROLE_USER', '普通用户，拥有部分权限');

-- 权限/资源表 (支持动态 URL 校验) permission
drop table if exists permissions;
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL, -- 权限名称，如 "用户删除"
    url VARCHAR(255), -- 资源路径，如 "/api/users/**"
    method VARCHAR(10) NOT NULL, -- 请求方法，如 "DELETE" 或 "ALL"
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE permissions IS '权限/资源表 (支持动态 URL 校验)';
-- 字段注释
COMMENT ON COLUMN permissions.id IS '权限ID';
COMMENT ON COLUMN permissions.name IS '权限名称';
COMMENT ON COLUMN permissions.url IS '资源路径';
COMMENT ON COLUMN permissions.method IS '请求方法';
COMMENT ON COLUMN permissions.description IS '权限描述';
COMMENT ON COLUMN permissions.created_at IS '创建时间';
COMMENT ON COLUMN permissions.created_by IS '创建人';
COMMENT ON COLUMN permissions.updated_at IS '更新时间';
COMMENT ON COLUMN permissions.updated_by IS '更新人';
-- 索引
CREATE INDEX idx_permission_url ON permissions(url);

-- 用户-角色关联表
drop table if exists user_roles;
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE user_roles IS '用户-角色关联表';
-- 字段注释
COMMENT ON COLUMN user_roles.id IS '关联ID';
COMMENT ON COLUMN user_roles.user_id IS '用户ID';
COMMENT ON COLUMN user_roles.role_id IS '角色ID';
COMMENT ON COLUMN user_roles.created_at IS '创建时间';
COMMENT ON COLUMN user_roles.created_by IS '创建人';
COMMENT ON COLUMN user_roles.updated_at IS '更新时间';
COMMENT ON COLUMN user_roles.updated_by IS '更新人';
-- 索引
CREATE INDEX idx_user_role_user_id ON user_roles(user_id);
CREATE INDEX idx_user_role_role_id ON user_roles(role_id);
-- 添加基础数据
insert into user_roles(id, user_id, role_id) values(1, 1, 1);

-- 角色-权限关联表
drop table if exists role_permissions;
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT
);
-- 表注释
COMMENT ON TABLE role_permissions IS '角色-权限关联表';
-- 字段注释
COMMENT ON COLUMN role_permissions.id IS '关联ID';
COMMENT ON COLUMN role_permissions.role_id IS '角色ID';
COMMENT ON COLUMN role_permissions.permission_id IS '权限ID';
COMMENT ON COLUMN role_permissions.created_at IS '创建时间';
COMMENT ON COLUMN role_permissions.created_by IS '创建人';
COMMENT ON COLUMN role_permissions.updated_at IS '更新时间';
COMMENT ON COLUMN role_permissions.updated_by IS '更新人';
-- 索引
CREATE INDEX idx_role_permission_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permissions(permission_id);