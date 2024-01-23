package com.lizumin.wms.contoller;

import com.lizumin.wms.controller.UserController;
import com.lizumin.wms.dao.RedisOperator;
import com.lizumin.wms.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class UserControllerTest {
    @MockBean
    private MailService mailService;
    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private MockMvc mvc;

    private MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

    @BeforeEach
    public void clearParameters() {
        parameters.clear();
    }

    /**
     * username/email不合法时测试usernameCheck emailCheck sendEmailVerifyCode
     *
     */
    @Test
    public void should_get_bad_request_when_username_or_email_illegal() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/username").queryParams(parameters))
                .andExpect(status().isBadRequest());
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/email").queryParams(parameters)).
                andExpect(status().isBadRequest());

        parameters.set("username", "1a.~");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/username").queryParams(parameters))
                .andExpect(status().isBadRequest());

        parameters.remove("username");
        parameters.set("email", "1a123@");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/email").queryParams(parameters))
                .andExpect(status().isBadRequest());
        this.mvc.perform(MockMvcRequestBuilders.get("/user/code/email").queryParams(parameters))
                .andExpect(status().isBadRequest());
    }

    /**
     * username/email不在数据库时usernameCheck/emailCheck
     *
     */
    @Test
    public void should_return_false_when_username_or_email_not_in_db() throws Exception {
        parameters.set("username", "notexist");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/username").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("false")));

        parameters.remove("username");
        parameters.set("email", "notexist@test.com");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/email").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("false")));
    }

    /**
     * username/email在数据库时usernameCheck/emailCheck
     *
     */
    @Test
    public void should_return_true_when_username_or_email_exist_in_db() throws Exception {
        parameters.set("username", "test001");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/username").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("true")));

        parameters.remove("username");
        parameters.set("email", "test@test.com");
        this.mvc.perform(MockMvcRequestBuilders.get("/user/check/email").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("true")));
    }

    /**
     * 请求过多拦截测试
     *
     */
    @Test
    public void should_throw_429_when_request_to_many() throws Exception {
        parameters.set("username", "badtest");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user/check/username").queryParams(parameters)
                .with(remoteHost("100.100.100.100"));

        for (int i = 0; i < 10; i++) {
            this.mvc.perform(requestBuilder);
        }

        this.mvc.perform(requestBuilder).andExpect(status().is(429));
        this.mvc.perform(requestBuilder).andExpect(status().is(429));
    }

    /**
     * 发送邮件验证测试
     *
     */
    @Test
    public void should_send_email_when_request_to_email_code() throws Exception {
        parameters.set("email", "test2@test.com");

        this.mvc.perform(MockMvcRequestBuilders.get("/user/code/email").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("success")));
        verify(mailService).sendVerifyCode(anyString(), anyString(), ArgumentMatchers.any(Locale.class));
        Date expire = redisOperator.getExpire("email_verify_code_test2@test.com");
        assertThat(expire, notNullValue());
        long diff = expire.getTime() - System.currentTimeMillis();
        assertThat(diff < 1000, is(true));
    }

    /**
     * 参数不正确时，邮件重置密码验证测试
     *
     */
    @Test
    public void should_send_bad_request_when_reset_password_with_email_giving_illegal_parameter() throws Exception {
        // 1. 参数为空
        this.mvc.perform(MockMvcRequestBuilders.post("/user/reset/email").queryParams(parameters))
                .andExpect(status().isBadRequest());

        // 2. 参数不合法
        parameters.set("email", "notaemail");
        parameters.set("password", "a1");
        parameters.set("code", "a1234");
        this.mvc.perform(MockMvcRequestBuilders.post("/user/reset/email").queryParams(parameters))
                .andExpect(status().isBadRequest()).andExpect(content().string(equalTo("邮箱格式不正确")));

        parameters.set("email", "testresetemail@test.com");
        this.mvc.perform(MockMvcRequestBuilders.post("/user/reset/email").queryParams(parameters))
                .andExpect(status().isBadRequest()).andExpect(content().string(equalTo("密码格式不正确(长度必须大于8小于16)")));

        // 3. code不存在cache
        parameters.set("email", "test@test.com");
        parameters.set("password", "a123456789");
        this.mvc.perform(MockMvcRequestBuilders.post("/user/reset/email").queryParams(parameters))
                .andExpect(status().isBadRequest()).andExpect(content().string(equalTo("邮件验证码无效")));
    }

    /**
     * 邮件重置密码验证测试
     *
     */
    @Test
    public void should_set_encrypted_password_when_reset_password_with_email() throws Exception {
        parameters.set("email", "testmodified@test.com");
        parameters.set("password", "a123456789");
        parameters.set("code", "a12345");
        redisOperator.set(UserController.EMAIL_VERIFY_CODE_PREFIX + "testmodified@test.com", "a12345");

        this.mvc.perform(MockMvcRequestBuilders.post("/user/reset/email").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(content().string(equalTo("success")));
    }

    // 修改请求ip
    private static RequestPostProcessor remoteHost(final String remoteHost) {
        return request -> {
            request.setRemoteAddr(remoteHost);
            return request;
        };
    }
}
