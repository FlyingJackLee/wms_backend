package com.lizumin.wms.tools;

import com.lizumin.wms.tool.ResponseTool;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


import static com.lizumin.wms.tool.ResponseTool.*;

public class ResponseToolTest {
    /**
     * 测试写入数据为空时response设置是否为空
     *
     */
    @Test
    public void should_get_empty_json_when_body_is_null_or_empty() throws IOException, NoSuchFieldException, IllegalAccessException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeJson(response, null);
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("null"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeJson(response, new HashMap<>(0));
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{}"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeValue(response, null, null);
        assertThat(getBody(response), equalTo(""));
    }

    /**
     * 测试正常情况下json写入response
     *
     */
    @Test
    public void should_get_json_when_body_is_legal() throws IOException, NoSuchFieldException, IllegalAccessException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        Map<String, String> body = new HashMap<>(2);
        body.put("test001", "value001");
        body.put("test002", "value002");
        writeJson(response, body);
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{\"test001\":\"value001\",\"test002\":\"value002\"}"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeValue(response, "test001", "test002");
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{\"test001\":\"test002\"}"));
    }

    /**
     * 测试设置纯文本返回体
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void should_write_str_into_body_when_body_is_legal() throws NoSuchFieldException, IllegalAccessException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        ResponseTool.writeBody(response, "success");
        assertThat(response.getContentType(), containsString("text/plain"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("success"));
    }

    private String getBody(MockHttpServletResponse response) throws NoSuchFieldException, IllegalAccessException {
        Field contentField = response.getClass().getDeclaredField("content");
        contentField.setAccessible(true);
        return ((ByteArrayOutputStream) contentField.get(response)).toString();
    }
}
