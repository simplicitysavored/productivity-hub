package xyz.yuanjin.project.productivityhub.bootstrap.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 11:11</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    public LogAspect() {
        System.out.println(">>>>> LogAspect 切面类已初始化 <<<<<");
    }

    @Around("execution(* xyz.yuanjin.project.productivityhub.api.controller..*.*(..))")
    public Object doControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = null;
        Object result;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            request = attributes.getRequest();

            result = joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("API请求 | URL: {} | 方法: {}.{} | 耗时: {}ms",
                    request == null ? "" : request.getRequestURI(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    (end - start));
        }

        return result;
    }
}
