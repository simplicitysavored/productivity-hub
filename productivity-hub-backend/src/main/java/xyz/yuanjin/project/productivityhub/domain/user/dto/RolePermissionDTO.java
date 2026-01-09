package xyz.yuanjin.project.productivityhub.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/6 15:26</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
public class RolePermissionDTO {
    private String url;         // 资源路径，如 /api/tasks/**
    private String method;      // 请求方法，如 GET, POST, ALL
    private String roleCodes;   // 拥有该权限的角色编码集合，逗号分隔，如 "ROLE_ADMIN,ROLE_USER"
}
