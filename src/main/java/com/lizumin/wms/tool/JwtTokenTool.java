package com.lizumin.wms.tool;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 用户生成和验证jwt token的工具， 务必设置好key路径的环境变量
 *
 */
public class JwtTokenTool {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenTool.class);
    private static final int DEFAULT_TOKEN_EXPIRE_HOURS = 24;

    private static final String PRIVATE_ENV = "JWT_PRIVATE_KEY_PATH";
    private static final String PUBLIC_ENV = "JWT_PUBLIC_KEY_PATH";

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    static {
         // Read private and public key path from system env.
         Map<String, String> env = System.getenv();
         if (!env.containsKey(PRIVATE_ENV) || !env.containsKey(PUBLIC_ENV)){
             String msg = "Missing PRIVATE_ENV/PUBLIC_ENV system setting - set system env JWT_PRIVATE_KEY_PATH and JWT_PUBLIC_KEY_PATH";
             logger.error(msg);
             logger.info("Current envs: {}", env);
             throw new RuntimeException(msg);
         }
         String privateKeyPath = env.get(PRIVATE_ENV);
         String publicKeyPath = env.get(PUBLIC_ENV);

         File privateFile = new File(privateKeyPath);
         try(FileInputStream  inputStream = new FileInputStream(privateFile)) {
             // Convert key string to standard private string
             String key = new String(inputStream.readAllBytes(), Charset.defaultCharset());
             String privateKeyPEM = key
                     .replace("-----BEGIN PRIVATE KEY-----", "")
                     .replaceAll("\n", "")
                     .replaceAll("\r", "")
                     .replace("-----END PRIVATE KEY-----", "");
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(spec);
        } catch (IOException e) {
            logger.error("cannot find private_key.pem");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("read private_key.pem failed");
            throw new RuntimeException(e);
        }

        File publicFile = new File(publicKeyPath);
        try(FileInputStream inputStream = new FileInputStream(publicFile)){
            // Convert key string to standard public string
            String key = new String(inputStream.readAllBytes(), Charset.defaultCharset());
            String publicKeyPEM = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("\n", "")
                    .replaceAll("\r", "")
                    .replace("-----END PUBLIC KEY-----", "");
            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
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
     * @param username: 用户名
     * @param id : 用户id
     * @param expireDate : 过期日期
     * @return jwt字符串
     */
    public static String generateToken(String username, long id, Date expireDate) {
        return Jwts.builder().claim("id", id)
                .audience().add(username).and()
                .expiration(expireDate).issuedAt(new Date())
                .signWith(privateKey)
                .compact();
    }

    /**
     * 生成JWT（过期日期DEFAULT_TOKEN_EXPIRE_HOURS）
     *
     * @param id : 用户id
     * @return jwt字符串
     */
    public static String generateToken(String username, long id) {
        Calendar calendar = Calendar.getInstance();
        // 默认有效期
        calendar.add(Calendar.HOUR_OF_DAY, DEFAULT_TOKEN_EXPIRE_HOURS);

        return generateToken(username, id, calendar.getTime());
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
