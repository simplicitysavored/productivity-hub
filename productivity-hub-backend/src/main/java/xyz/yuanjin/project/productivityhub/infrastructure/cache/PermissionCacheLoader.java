package xyz.yuanjin.project.productivityhub.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.productivityhub.domain.user.dto.RolePermissionDTO;
import xyz.yuanjin.project.productivityhub.infrastructure.repository.PermissionMapper;

import java.util.List;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/6 15:20</p >
 * <p>CommandLineRunner 是一个极其简单但功能强大的接口。它的核心作用是：在 Spring 容器启动完成（所有 Bean 都加载并初始化完毕）后，自动执行一段特定的逻辑。</p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCacheLoader implements CommandLineRunner {
    private final PermissionMapper permissionMapper;
    private final StringRedisTemplate redisTemplate;
    public static final String KEY = "auth:permissions";

    @Override
    public void run(String... args) {
        // 查询数据库：获取所有权限及其对应的角色列表
        // 建议 SQL: SELECT p.url, p.method, r.role_code FROM permissions p ...
        List<RolePermissionDTO> list = permissionMapper.findAllRolePermissions();
        log.info("启动时，获取所有权限及其对应的角色，共{} 条", list.size());

        redisTemplate.delete(KEY);
        list.forEach(item -> {
            // Redis Hash 结构：Field 为 "URL:METHOD", Value 为 "ROLE_A,ROLE_B"
            String field = item.getUrl() + ":" + item.getMethod();
            redisTemplate.opsForHash().put(KEY, field, item.getRoleCodes());
            log.debug("权限缓存到 Redis：key={}, value={}", field, item.getRoleCodes());
        });
    }
}
