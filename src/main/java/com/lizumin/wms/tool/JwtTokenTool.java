package com.lizumin.wms.tool;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.security.*;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class JwtTokenTool {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenTool.class);
    private static final String PRIVATE_KEY_PATH = "private.key";
    private static final String PUBLIC_KEY_PATH = "public.key";
    private static final int DEFAULT_TOKEN_EXPIRE_HOURS = 24;

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

     // Read private and public key from file.
    static {
        try {
            InputStream inputStream = new ClassPathResource(PRIVATE_KEY_PATH).getInputStream();
            byte[] keyBytes = inputStream.readAllBytes();

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        } catch (IOException e) {
            logger.error("cannot find private_key.pem");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("read private_key.pem failed");
            throw new RuntimeException(e);
        }

        try {
            InputStream inputStream = new ClassPathResource(PUBLIC_KEY_PATH).getInputStream();
            byte[] keyBytes = inputStream.readAllBytes();

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            publicKey = kf.generatePublic(spec);
        } catch (IOException e) {
            logger.error("cannot find private key.pem");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("read private key failed");
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成JWT
     *
     * @param audience : 用户名
     * @param expireDate : 过期日期
     * @return jwt字符串
     */
    public static String generateToken(String audience, Date expireDate) {
        return Jwts.builder()
                .audience().add(audience).and().expiration(expireDate).issuedAt(new Date())
                .signWith(privateKey)
                .compact();
    }

    /**
     * 生成JWT（过期日期DEFAULT_TOKEN_EXPIRE_HOURS）
     *
     * @param audience : 用户名
     * @return jwt字符串
     */
    public static String generateToken(String audience) {
        Calendar calendar = Calendar.getInstance();
        // 默认有效期
        calendar.add(Calendar.HOUR_OF_DAY, DEFAULT_TOKEN_EXPIRE_HOURS);

        return generateToken(audience, calendar.getTime());
    }

    /**
     * 解析jwt并返回claims
     *
     * @param token jwt
     * @return  空Optional表示解析失败或者jwt已过期
     */
    public static Optional<Jws<Claims>> verifyAndGetClaims(String token) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e){
            logger.debug("token expired");
            return Optional.empty();
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
        return Optional.of(claims);
    }
}
