package xyz.yuanjin.project.productivityhub.common.core;

import lombok.Getter;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:07</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或有效身份已过期"),
    FORBIDDEN(403, "没有相关权限"),
    FAILED(500, "操作失败"),
    ERROR(999, "系统未知错误");

    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
