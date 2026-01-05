package xyz.yuanjin.project.productivityhub.common.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

/**
 * <p>标题： JWT 全局通用工具 </p >
 * <p>功能： 利用 nimbus-jose-jwt 库生成 Token、解析 Token 以及校验 Token 有效性。</p >
 * <p>创建日期：2026/1/4 09:34</p >
 *
 * 作者：yuanjin
 *
 * @version 1.0
 */
@Slf4j
public class JwtUtils {
    // 密钥：HS256 算法要求密钥至少 256 位（32 字符）
    private static final String SECRET = "your-256-bit-secret-key-at-least-32-chars-long";
    // 有效期：设置为 24 小时
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 根据用户 ID 生成 Token
     */
    public static String createToken(String userId) {
        try {
            // 生成一个唯一的 JTI
            String jti = UUID.randomUUID().toString().replace("-", "");

            // 1. 创建 JWT 持有载荷 (Payload)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId) // 将用户 ID 存入 sub 字段
                    .jwtID(jti) // [关键] 存入唯一标识
                    .issuer("productivity-hub")
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                    .build();

            // 2. 创建签名器
            JWSSigner signer = new MACSigner(SECRET);

            // 3. 构建签名 JWT 对象
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            // 4. 执行签名并返回字符串
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("JWT Token 生成失败", e);
            throw new RuntimeException("登录认证失败");
        }
    }

    /**
     * 校验 Token 并解析出用户 ID
     */
    public static String parseToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);

            // 校验签名和过期时间
            if (!signedJWT.verify(verifier)) {
                return null;
            }
            if (new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                return null;
            }

            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException | JOSEException e) {
            log.error("JWT Token 解析失败", e);
            return null;
        }
    }

    // 增加解析 JTI 的方法
    public static String getJti(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            return null;
        }
    }
}
