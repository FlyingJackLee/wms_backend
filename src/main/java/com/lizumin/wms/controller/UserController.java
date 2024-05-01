package com.lizumin.wms.controller;

import com.lizumin.wms.aop.RequestRateLimit;
import com.lizumin.wms.entity.ApiRes;
import com.lizumin.wms.service.MailService;
import com.lizumin.wms.service.RedisOperatorImpl;
import com.lizumin.wms.service.SmsService;
import com.lizumin.wms.service.UserService;
import com.lizumin.wms.tool.MessageUtil;
import com.lizumin.wms.tool.Verify;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Locale;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    public final static String EMAIL_VERIFY_CODE_PREFIX = "email_verify_code_";
    private final static int EMAIL_VERIFY_CODE_DURATION = 15;
    public final static String PHONE_VERIFY_CODE_PREFIX = "phone_verify_code_";
    private final static int PHONE_VERIFY_CODE_DURATION = 5;
    private final static int VERIFY_CODE_LENGTH = 6;
    private MailService mailService;
    private RedisOperatorImpl redisOperator;
    private UserService userService;
    private SmsService smsService;

    @Value("${aliyun.access.signName}")
    private String signName;

    @Value("${aliyun.access.templateCode}")
    private String templateCode;

    public UserController(MailService mailService, RedisOperatorImpl redisOperator, UserService userService, SmsService smsService) {
        this.mailService = mailService;
        this.redisOperator = redisOperator;
        this.userService = userService;
        this.smsService = smsService;
    }

    @GetMapping("/code/phone")
    public ResponseEntity<ApiRes> phoneVerifyCode(@RequestParam("phone") String phone) {
        if (!Verify.verifyPhoneNumber(phone)){
            return ResponseEntity.badRequest().body(ApiRes.fail("手机号码格式不正确"));
        }
        // 1. 生成6位数验证码
        String code = generateDigitalCode();

        boolean isSuccess = true;

        // 2. 发送短信
        try {
            isSuccess = this.smsService.sendSms(
                    phone,
                    String.format("{\"code\":\"%s\"}", code),
                    signName,
                    templateCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiRes.fail("短信接口错误，请联系管理员"));
        }

        // 3. 存入缓存
        redisOperator.set(PHONE_VERIFY_CODE_PREFIX + phone,
                code,
                Duration.ofMinutes(PHONE_VERIFY_CODE_DURATION));

        if(isSuccess) {
            return ResponseEntity.ok().body(ApiRes.success());
        } else {
            return ResponseEntity.badRequest().body(ApiRes.fail("短信接口错误，请联系管理员"));
        }
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
     * 检测phone是否存在（限制次数访问）
     *
     * @param phone
     * @return true 已存在
     *         false 不存在
     */
    @RequestRateLimit
    @GetMapping("/check/phone")
    public ResponseEntity<Boolean> phoneCheck(@RequestParam("phone") String phone) {
        if (!Verify.verifyPhoneNumber(phone)) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok().body(this.userService.isPhoneExist(phone));
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
    public ResponseEntity<ApiRes> sendEmailVerifyCode(@RequestParam("email") String email) {
        if (!Verify.verifyEmail(email)) {
            return ResponseEntity.badRequest().body(
                    ApiRes.fail(MessageUtil.getMessageByContext("BPV-008"))
            );
        }
        String code = generateCode(VERIFY_CODE_LENGTH);
        // 验证码15分钟过期
        redisOperator.set(EMAIL_VERIFY_CODE_PREFIX+ email, code, Duration.ofMinutes(EMAIL_VERIFY_CODE_DURATION));
        Locale locale = LocaleContextHolder.getLocale();
        this.mailService.sendVerifyCode(email, code, locale);

        return ResponseEntity.ok(ApiRes.success());
    }

    /**
     * 通过邮件修改密码
     *
     * @param email 发送地址
     *        code  邮件验证码（通过sendEmailVerifyCode发送）
     * @return
     */
    @PostMapping("/reset/email")
    public ResponseEntity<String> resetPasswordByEmail(@RequestParam("email") String email,
                                                       @RequestParam("password") String password,
                                                        @RequestParam("code") String code) {
        if (!Verify.verifyEmail(email)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-008"));
        }
        if (!Verify.verifyPassword(password)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-010"));
        }

        // 邮箱不存在时停止
        if (!userService.isEmailExist(email)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-015"));
        }

        // 验证码不存在时不允许验证
        String verifyCode = redisOperator.get(UserController.EMAIL_VERIFY_CODE_PREFIX + email);
        if (verifyCode == null || !verifyCode.equals(code)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-012"));
        }

        this.userService.resetPasswordByEmail(email, password);
        return ResponseEntity.ok ("success");
    }

    /**
     * 通过手机修改密码
     *
     * @param phone 发送地址
     *        code  邮件验证码（通过sendEmailVerifyCode发送）
     * @return
     */
    @PostMapping("/reset/phone")
    public ResponseEntity<String> resetPasswordByPhone(@RequestParam("phone") String phone,
                                                       @RequestParam("password") String password,
                                                       @RequestParam("code") String code) {
        if (!Verify.verifyPhoneNumber(phone)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-019"));
        }
        if (!Verify.verifyPassword(password)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-010"));
        }

        // 邮箱不存在时停止
        if (!userService.isPhoneExist(phone)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-020"));
        }

        // 验证码不存在时不允许验证
        String verifyCode = redisOperator.get(UserController.PHONE_VERIFY_CODE_PREFIX + phone);
        if (verifyCode == null || !verifyCode.equals(code)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-012"));
        }

        this.userService.resetPasswordByPhone(phone, password);
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

    /**
     * Generate 6-length random digital code
     *
     * @return
     */
    private String generateDigitalCode() {
        double randomNumber = Math.random();
        int code = (int)(randomNumber * 900000) + 100000;
        return Integer.toString(code);
    }
}
