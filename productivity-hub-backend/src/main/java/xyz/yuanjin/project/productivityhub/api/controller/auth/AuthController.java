package xyz.yuanjin.project.productivityhub.api.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.yuanjin.project.productivityhub.api.dto.UserLoginDTO;
import xyz.yuanjin.project.productivityhub.api.dto.UserRegisterDTO;
import xyz.yuanjin.project.productivityhub.application.service.UserApplicationService;
import xyz.yuanjin.project.productivityhub.common.core.R;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 09:34</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    public R<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        userApplicationService.register(dto);
        return R.success("注册成功");
    }

    @PostMapping("/login")
    public R<String> login(@Valid @RequestBody UserLoginDTO dto) {
        String token = userApplicationService.login(dto);
        return R.success(token);
    }

}
