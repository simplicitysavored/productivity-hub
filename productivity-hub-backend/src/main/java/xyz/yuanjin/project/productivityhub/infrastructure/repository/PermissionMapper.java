package xyz.yuanjin.project.productivityhub.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.yuanjin.project.productivityhub.domain.user.dto.RolePermissionDTO;
import xyz.yuanjin.project.productivityhub.domain.user.entity.Permission;

import java.util.List;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/6 15:23</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    @Select("""
                SELECT
                    p.url,
                    p.method,
                    string_agg(r.role_code, ',') AS roleCodes
                FROM
                    permissions p
                INNER JOIN role_permissions rp ON p.id = rp.permission_id
                INNER JOIN roles r ON rp.role_id = r.id
                GROUP BY p.url, p.method
            """)
    List<RolePermissionDTO> findAllRolePermissions();
}
