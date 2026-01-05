package xyz.yuanjin.project.productivityhub.common.core;

import lombok.Getter;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:09</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
public class ApiException extends RuntimeException {
    private final ResultCode resultCode;

    public ApiException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ApiException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILED;
    }
}
