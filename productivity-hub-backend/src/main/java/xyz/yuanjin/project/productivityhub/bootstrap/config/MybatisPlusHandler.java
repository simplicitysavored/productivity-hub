package xyz.yuanjin.project.productivityhub.bootstrap.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import xyz.yuanjin.project.productivityhub.domain.user.entity.CustomUserDetails;

import java.time.OffsetDateTime;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 09:42</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Component
public class MybatisPlusHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时自动填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createdAt", OffsetDateTime.class, OffsetDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", OffsetDateTime.class, OffsetDateTime.now());
        // 获取登陆人
        Long userId = getUserId();
        this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updatedBy", Long.class, userId);
    }

    private static Long getUserId() {
        Long userId = null;
        CustomUserDetails details = null;
        try {
            details = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userId = details.getUserId();
        } catch (Exception e) {
            userId = 0L;
            log.warn("获取登陆人失败，使用默认用户ID: {}", userId);
        }
        return userId;
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充更新时间
        this.strictUpdateFill(metaObject, "updatedAt", OffsetDateTime.class, OffsetDateTime.now());
        // 获取登陆人
        Long userId = getUserId();
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
    }
}
