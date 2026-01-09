package xyz.yuanjin.project.productivityhub.bootstrap.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 10:28</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    public WebConfig() {
        log.debug("加载 WebConfig...");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            // 针对 JSON 转换器强制设为 UTF-8
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setDefaultCharset(StandardCharsets.UTF_8);
                // 下面这行限制了 Jackson 转换器只支持 APPLICATION_JSON，但 /actuator/health 返回的默认 Content-Type 并不是标准的 application/json，而是 application/vnd.spring-boot.actuator.v3+json（或其他类似版本格式）。
                // 不需要限制 SupportedMediaTypes，Jackson 默认就能处理绝大多数 JSON 变体。
                //jacksonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
            }
        }
    }
}
