package com.lizumin.wms.controller;

import com.lizumin.wms.dao.RedisOperator;
import com.lizumin.wms.entity.SystemAuthority;
import com.lizumin.wms.entity.User;
import com.lizumin.wms.service.UserService;
import com.lizumin.wms.tool.MessageUtil;
import com.lizumin.wms.tool.Verify;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/signup")
public class SignUpController {
    private UserService userService;

    private PasswordEncoder passwordEncode;
    private RedisOperator redisOperator;

    public SignUpController(UserService userService, PasswordEncoder passwordEncode, RedisOperator redisOperator) {
        this.userService = userService;
        this.passwordEncode = passwordEncode;
        this.redisOperator = redisOperator;
    }

    /**
     * 根据username注册
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/username")
    public ResponseEntity<String> signup(@RequestParam("username") String username,
                       @RequestParam("password") String password) {
        if (!Verify.verifyUsername(username)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-009"));
        }
        if (!Verify.verifyPassword(password)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-010"));
        }

        // 新建账户流程
        User userAdd = new User(username, this.passwordEncode.encode(password)); // 默认为0的group
        userAdd.setAuthorities(SystemAuthority.defaults()); // 角色设置为默认 - 未加入group， 无权限

        try {
            this.userService.insertUser(userAdd);
        } catch (DuplicateKeyException e){
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-011"));
        }
        return ResponseEntity.ok("success");
    }

    /**
     * 根据email注册 (username自动生成)
     *
     * @param email
     * @param password
     * @param code
     * @return
     */
    @PostMapping("/email")
    public ResponseEntity<String> signupWithEmail(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("code") String code) {
        if (!Verify.verifyEmail(email)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-008"));
        }
        if (!Verify.verifyPassword(password)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-010"));
        }

        // 验证码不存在时不允许验证
        String verifyCode = redisOperator.get(UserController.EMAIL_VERIFY_CODE_PREFIX + email);
        if (verifyCode == null || !verifyCode.equals(code)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-012"));
        }

        // 邮箱已被使用时停止
        if (userService.isEmailExist(email)) {
            return ResponseEntity.badRequest().body(MessageUtil.getMessageByContext("BPV-013"));
        }

        String username = generateUniqueUsername();

        User userAdd = new User(username, this.passwordEncode.encode(password));// 默认为0的group
        userAdd.setAuthorities(SystemAuthority.defaults()); // 角色设置为默认 - 未加入group， 无权限

        try {
            this.userService.insertUser(userAdd, email, null);
        } catch (DuplicateKeyException e){
            // 极低概率
            return ResponseEntity.internalServerError().body(MessageUtil.getMessageByContext("BSA-014"));
        }

        return ResponseEntity.ok("success");
    }

    /**
     * 通过UUID生成一个唯一username（为防止无限循环设置了尝试上限，有极低概率生成失败）
     */
    private String generateUniqueUsername() {
        String username = UUID.randomUUID().toString();

        int attempt = 0;
        int maxAttempts = 10;
        while (userService.isUsernameExist(username) && attempt < maxAttempts) {
            username = UUID.randomUUID().toString();
            maxAttempts++;
        }
        return username;
    }
}
