package xyz.yuanjin.project.productivityhub.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>标题： </p >
 * <p>功能： </p >
 * <p>创建日期：2026/1/4 15:13</p >
 * <p>
 * 作者：yuanjin
 *
 * @version 1.0
 */
public class IpUtils {
    private static final String UNKNOWN = "unknown";

    /**
     * 获取当前请求的真实 IP 地址
     */
    public static String getRemoteIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "0.0.0.0";
        }
        return getRemoteIp(attributes.getRequest());
    }

    /**
     * 根据请求对象获取真实 IP
     */
    public static String getRemoteIp(HttpServletRequest request) {
        // 1. 尝试从常见的代理头中获取
        String ip = request.getHeader("x-forwarded-for");
        if (!isValidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!isValidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!isValidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!isValidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
            // 如果是本地回环地址，根据网卡取本机配置的真实IP
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    ip = "127.0.0.1";
                }
            }
        }

        // 2. 多级代理下，x-forwarded-for 的第一个值才是真实客户端 IP
        if (StringUtils.hasText(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private static boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
