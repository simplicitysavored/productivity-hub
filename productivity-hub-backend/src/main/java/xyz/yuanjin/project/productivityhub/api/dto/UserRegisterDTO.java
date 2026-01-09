package xyz.yuanjin.project.productivityhub.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:01</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;
}
