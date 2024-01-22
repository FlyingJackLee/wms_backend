package com.lizumin.wms.tools;

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
        assertThat(response.getContentType(), equalTo("application/json; charset=utf-8"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("null"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeJson(response, new HashMap<>(0));
        assertThat(response.getContentType(), equalTo("application/json; charset=utf-8"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{}"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeValue(response, null, null);
        assertThat(response.getContentType(), equalTo("application/json; charset=utf-8"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
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
        assertThat(response.getContentType(), equalTo("application/json; charset=utf-8"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{\"test001\":\"value001\",\"test002\":\"value002\"}"));

        response = new MockHttpServletResponse();
        response.setStatus(MockHttpServletResponse.SC_OK);
        writeValue(response, "test001", "test002");
        assertThat(response.getContentType(), equalTo("application/json; charset=utf-8"));
        assertThat(response.getCharacterEncoding(), equalTo("UTF-8"));
        assertThat(getBody(response), equalTo("{\"test001\":\"test002\"}"));
    }

    private String getBody(MockHttpServletResponse response) throws NoSuchFieldException, IllegalAccessException {
        Field contentField = response.getClass().getDeclaredField("content");
        contentField.setAccessible(true);
        return ((ByteArrayOutputStream) contentField.get(response)).toString();
    }
}
