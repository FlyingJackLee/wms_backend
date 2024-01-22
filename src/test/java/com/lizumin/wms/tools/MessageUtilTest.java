package com.lizumin.wms.tools;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;


import static com.lizumin.wms.tool.MessageUtil.*;

@SpringBootTest
public class MessageUtilTest {
    /**
     * 测试i18项不存在时获取翻译，此时返回原信息
     *
     */
    @Test
    public void should_original_code_when_message_not_exist() {
        String result = getMessageByLocale("not_exist", Locale.CHINA);
        assertThat(result, equalTo("not_exist"));

        result = getMessageByContext("not_exist");
        assertThat(result, equalTo("not_exist"));

        result = getMessageByDefault("not_exist");
        assertThat(result, equalTo("not_exist"));

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Accept-Language")).thenReturn(Locale.CHINA.toString());
        result = getMessageByRequest("not_exist" , req);
        assertThat(result, equalTo("not_exist"));
    }

    /**
     * 测试返回存在的翻译信息
     *
     */
    @Test
    public void should_message_when_code_is_right() {
        String cnResult = getMessageByLocale("UNKNOWN", Locale.CHINA);
        assertThat(cnResult, equalTo("未知错误"));
        String enResult = getMessageByLocale("UNKNOWN", Locale.US);
        assertThat(enResult, equalTo("Unknown problem"));

        cnResult = getMessageByContext("UNKNOWN");
        assertThat(cnResult, equalTo("未知错误"));

        cnResult = getMessageByDefault("UNKNOWN");
        assertThat(cnResult, equalTo("未知错误"));

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getHeader("Accept-Language")).thenReturn(Locale.CHINA.toString());
        cnResult = getMessageByRequest("UNKNOWN" , req);
        assertThat(cnResult, equalTo("未知错误"));

        when(req.getHeader("Accept-Language")).thenReturn(Locale.US.toString());
        enResult = getMessageByRequest("UNKNOWN", req);
        assertThat(enResult, equalTo("Unknown problem"));
    }
}
