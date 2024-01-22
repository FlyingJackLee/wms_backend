package com.lizumin.wms.service;

import com.lizumin.wms.dao.RedisOperator;
import com.lizumin.wms.tool.MessageUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.util.Locale;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    public final static String EMAIL_CACHE_PREFIX = "Email_Send_";
    private final static int EMAIL_SEND_MAX_SECOND = 30; // 每30秒只能发送一封
    private JavaMailSender mailSender;
    private RedisOperator redisOperator;
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public MailService(JavaMailSender mailSender, RedisOperator redisOperator, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.redisOperator = redisOperator;
        this.templateEngine = templateEngine;
    }

    /**
     * 发送邮件，有30秒限制
     *
     * @param to
     * @param subject
     * @param content
     * @return 是否发送成功
     */
    public boolean sendMail(String to, String subject, String content) {
        // 距离上次发送还不到30秒
        if (redisOperator.hasKey(EMAIL_CACHE_PREFIX + to)) {
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
            // 发送成功，在缓存中记录
            redisOperator.set(EMAIL_CACHE_PREFIX + to, Duration.ofSeconds(EMAIL_SEND_MAX_SECOND));
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 发送验证码邮箱，有30秒限制
     *
     * @return 是否发送成功
     */
    public boolean sendVerifyCode(String to, String code, Locale locale) {
        locale = locale == null ? Locale.CHINA : locale;

        String subject = MessageUtil.getMessageByLocale("email.code.subject", locale);
        Context context = new Context(locale);
        context.setVariable("code", code);
        String emailContent = this.templateEngine.process("email_code.html", context);

        return sendMail(to, subject, emailContent);
    }
}
