package com.lizumin.wms.contoller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class LoginControllerTest {
    @Autowired
    private MockMvc mvc;
    private MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

    @BeforeEach
    public void clearParameters() {
        parameters.clear();
    }

    /**
     * /login/*请求字段缺失或不合法测试
     */
    @Test
    public void should_get_error_000_response_exception_when_login_with_empty_password() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").header("Accept-Language", "en-US")
                        .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("Missing required request parameter")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("请求字段错误")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("Missing required request parameter")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("请求字段错误")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("Missing required request parameter")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("请求字段错误")));
    }

    /**
     * 请求字段不合法测试
     */
    @Test
    public void should_get_error_when_parameter_illegal() throws Exception {
        parameters.set("email", "not_a_email");
        parameters.set("password", "123456");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("邮箱或密码不合法")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("Email or password is illegal")));

        parameters.remove("email");
        parameters.set("phone", "13000");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("手机号或密码不合法")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("Phone or password is illegal")));
    }

    /**
     * 请求字段不存在测试
     */
    @Test
    public void should_get_error_when_parameter_not_in_db() throws Exception {
        parameters.set("username", "not_exist");
        parameters.set("password", "123456");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("用户不存在")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username").header("Accept-Language", "en-US")
                        .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                                content().string(containsString("User not found")));

        parameters.remove("username");
        parameters.set("phone", "13000000000");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("用户不存在")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("User not found")));

        parameters.remove("phone");
        parameters.set("email", "not_exsit@test.com");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("手机号或密码不合法")));
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").header("Accept-Language", "en-US")
                .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                content().string(containsString("Phone or password is illegal")));
    }

    /**
     * 密码错误登陆测试
     */
    @Test
    public void should_get_bad_credential_when_password_is_wrong() throws Exception {
        parameters.set("phone", "13012341234");
        parameters.set("password", "12345678");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("Bad credentials")));

        parameters.remove("phone");
        parameters.set("email", "test@test.com");
        parameters.set("password", "12345678");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email")
                        .queryParams(parameters)).andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("Bad credentials")));

        parameters.remove("email");
        parameters.set("username", "test001");
        parameters.set("password", "12345678");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username").queryParams(parameters))
                .andExpect(status().is4xxClientError()).andExpect(
                        content().string(containsString("Bad credentials")));
    }

    /**
     * 成功登陆测试：返回token
     */
    @Test
    public void should_get_token_when_pass_authentication() throws Exception {
        parameters.set("phone", "13012341234");
        parameters.set("password", "abcd123");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/phone").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(
                        content().string(containsString("token")));

        parameters.remove("phone");
        parameters.set("email", "test@test.com");
        parameters.set("password", "abcd123");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/email").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(
                        content().string(containsString("token")));

        parameters.remove("email");
        parameters.set("username", "test001");
        parameters.set("password", "abcd123");
        this.mvc.perform(MockMvcRequestBuilders.post("/login/username").queryParams(parameters))
                .andExpect(status().is2xxSuccessful()).andExpect(
                        content().string(containsString("token")));
    }
}