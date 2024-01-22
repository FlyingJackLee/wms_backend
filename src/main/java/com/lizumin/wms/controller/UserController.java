package com.lizumin.wms.controller;

import com.lizumin.wms.aop.RequestRateLimit;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.service.MailService;
import com.lizumin.wms.service.RedisOperatorImpl;
import com.lizumin.wms.service.UserService;

import com.lizumin.wms.tool.MessageUtil;
import com.lizumin.wms.tool.Verify;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Locale;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    public final static String EMAIL_VERIFY_CODE_PREFIX = "email_verify_code_";
    private final static int EMAIL_VERIFY_CODE_DURATION = 15;
    private final static int VERIFY_CODE_LENGTH = 6;

    private MailService mailService;
    private RedisOperatorImpl redisOperator;
    private UserService userService;


    public UserController(MailService mailService, RedisOperatorImpl redisOperator, UserService userService) {
        this.mailService = mailService;
        this.redisOperator = redisOperator;
        this.userService = userService;
    }

    /**
     * 检测username是否存在（限制次数访问）
     *
     * @param username
     * @return true 已存在
     *         false 不存在
     */
    @RequestRateLimit
    @GetMapping("/check/username")
    public ResponseEntity<Boolean> usernameCheck(@RequestParam("username") String username) {
        if (!Verify.verifyUsername(username)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok().body(this.userService.isUsernameExist(username));
    }

    /**
     * 检测email是否存在 （限制次数访问）
     *
     * @param email
     * @return true 已存在
     *         false 不存在
     */
    @RequestRateLimit
    @GetMapping("/check/email")
    public ResponseEntity<Boolean> emailCheck(@RequestParam("email") String email) {
        if (!Verify.verifyEmail(email)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok().body(this.userService.isEmailExist(email));
    }


    /**
     * 发送随机验证邮件（15分钟）
     *
     * @param email 发送地址
     * @return 400 表示地址有误
     *         200 success 表示成功（注意即使15分钟未到期而导致未发送，也会显示success，以保证私密性）
     */
    @GetMapping("/code/email")
    public ResponseEntity<String> sendEmailVerifyCode(@RequestParam("email") String email) {
        if (!Verify.verifyEmail(email)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-008"));
        }
        String code = generateCode(VERIFY_CODE_LENGTH);
        // 验证码15分钟过期
        redisOperator.set(EMAIL_VERIFY_CODE_PREFIX+ email, code, Duration.ofMinutes(EMAIL_VERIFY_CODE_DURATION));
        Locale locale = LocaleContextHolder.getLocale();
        this.mailService.sendVerifyCode(email, code, locale);

        return ResponseEntity.ok ("success");
    }

    /**
     * Generate random code
     *
     * @param length
     * @return
     */
    private String generateCode(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = characters.charAt(random.nextInt(characters.length())); // 从字符集合中随机选取一个字符

            sb.append(c); // 将字符添加到结果字符串中
        }
        return sb.toString();
    }


}
