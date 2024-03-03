package com.lizumin.wms.service;

import com.lizumin.wms.dao.RedisOperator;

import com.lizumin.wms.tool.MessageUtil;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {
    private Map<String, Duration> mockCache = new HashMap<>(1);
    @Mock
    private RedisOperator redisOperator;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private MailService mailService;

    private String to = "test@test.com";


    @BeforeEach
    public void setCache() throws NoSuchFieldException, IllegalAccessException {
        Field field = MailService.class.getDeclaredField("from");
        field.setAccessible(true);
        field.set(mailService, "from@from.com");
    }

    /**
     * 测试已经存在cache的时候(也就是没超过30秒)
     */
    @Test
    public void should_return_false_when_cache_exist() {
        when(redisOperator.hasKey(anyString())).thenReturn(true);

        try(MockedStatic<MessageUtil> messageSource =  Mockito.mockStatic(MessageUtil.class)) {
            messageSource.when(()-> MessageUtil.getMessageByLocale(anyString(), any())).thenReturn("subject");
            boolean result = this.mailService.sendMail("a@a.com", "test", "test content");
            assertThat(result, is(false));

            result = this.mailService.sendVerifyCode("a@a.com", "test", Locale.CHINA);
            assertThat(result, is(false));
        }
    }

    /**
     * 测试已经存在cache的时候(也就是没超过30秒)
     */
    @Test
    public void should_return_true_and_send_when_cache_not_exist() {
        when(redisOperator.hasKey(anyString())).thenReturn(false);

        try(MockedStatic<MessageUtil> messageSource =  Mockito.mockStatic(MessageUtil.class)) {
            messageSource.when(()-> MessageUtil.getMessageByLocale(anyString(), any())).thenReturn("subject");

            MimeMessage mockMimeMessage = Mockito.mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
            when(redisOperator.hasKey(anyString())).thenReturn(false);

            boolean result = this.mailService.sendMail("a@a.com", "test", "test content");
            assertThat(result, is(true));
            verify(javaMailSender).send(any(MimeMessage.class));
            verify(redisOperator).set("Email_Send_a@a.com", Duration.ofSeconds(30));
        }
    }

}
