package com.lizumin.wms.contoller;

import com.lizumin.wms.controller.UserController;
import com.lizumin.wms.dao.RedisOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Duration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class SignUpControllerTest {
    @Autowired
    private MockMvc mvc;
    private MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

    @Autowired
    private RedisOperator redisOperator;

    @BeforeEach
    public void clearParameters() {
        parameters.clear();
    }

    /**
     *  phone注册输入参数有误时测试
     */
    @Test
    public void should_get_bad_request_when_username_or_password_illegal() throws Exception {
        //  空请求
        this.mvc.perform(MockMvcRequestBuilders.post("/signup/phone").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()).andReturn();

        // phone 不合法 （5-15位的小写字母和数字组成）
        this.mvc.perform(makeRequest("/signup/phone", "username", "", "password","123456789"))
                .andExpect(status().isBadRequest());
    }

    /**
     * phone注册已存在测试
     */
    @Test
    public void should_get_bad_request_when_user_has_exist_in_db() throws Exception {
        redisOperator.set(UserController.PHONE_VERIFY_CODE_PREFIX  + "13012341234" ,"a12345", Duration.ofSeconds(30));
        this.mvc.perform(makeRequest("/signup/phone", "phone", "13012341234", "password","abcdef123", "code", "a12345"))
                .andExpect(status().isBadRequest());
    }

    /**
     * phone注册测试
     */
    @Test
    public void should_get_new_user_id_when_insert_a_new_user() throws Exception {
        redisOperator.set(UserController.PHONE_VERIFY_CODE_PREFIX  + "13012341111" ,"a12345", Duration.ofSeconds(30));
        this.mvc.perform(makeRequest("/signup/phone", "phone", "13012341111", "password","abcdef123","code", "a12345"))
                .andExpect(status().is2xxSuccessful());
    }

    /**
     *  email注册输入参数有误时测试
     */
    @Test
    public void should_get_bad_request_when_parameter_illegal() throws Exception {
        // 1. 空请求
        this.mvc.perform(MockMvcRequestBuilders.post("/signup/email").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()).andReturn();

        // 2. email 不合法
        this.mvc.perform(makeRequest("/signup/email", "email", "", "password","123456789", "code", ""))
                .andExpect(status().isBadRequest());

        // 3. password 不合法（密码格式不正确(长度必须大于8小于16)）
        this.mvc.perform(makeRequest("/signup/email", "email", "bademailtest@test.com", "password","", "code", ""))
                .andExpect(status().isBadRequest());

        // 4. code不存在与cache
        this.mvc.perform(makeRequest("/signup/email", "email", "bademailtest@test.com", "password","abcd1234", "code", "a01234"))
                .andExpect(status().isBadRequest());

        //5. 传入code与cache code不一致
        redisOperator.set(UserController.EMAIL_VERIFY_CODE_PREFIX + "testemailcode@test.com" ,"a123c5", Duration.ofSeconds(30));
        this.mvc.perform(makeRequest("/signup/email", "email", "bademailtest@test.com", "password","abcd1234", "code", "a01234"))
                .andExpect(status().isBadRequest());

        //6. 邮箱被使用时
        redisOperator.set(UserController.EMAIL_VERIFY_CODE_PREFIX + "test@test.com" ,"a123c5", Duration.ofSeconds(30));
        this.mvc.perform(makeRequest("/signup/email", "email", "test@test.com", "password","abcd1234", "code", "a123c5"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Email正常注册
     *
     * @throws Exception
     */
    @Test
    public void should_insert_user_when_input_valid_username_code_password() throws Exception {
        redisOperator.set(UserController.EMAIL_VERIFY_CODE_PREFIX + "emailsignupsuccess@test.com" ,"a12345", Duration.ofSeconds(30));
        this.mvc.perform(makeRequest("/signup/email", "email", "emailsignupsuccess@test.com", "password","abcd1234", "code", "a12345"))
                .andExpect(status().is2xxSuccessful());

    }

    private MockHttpServletRequestBuilder makeRequest(String url, String key1, String value1, String key2, String value2) {
        return MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(key1, value1).param(key2, value2);
    }
    private MockHttpServletRequestBuilder makeRequest(String url, String key1, String value1, String key2, String value2,
                                                      String key3, String value3) {
        return MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(key1, value1).param(key2, value2).param(key3, value3);
    }
}