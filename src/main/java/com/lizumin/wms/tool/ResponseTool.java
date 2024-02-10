package com.lizumin.wms.tool;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.ContentType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ResponseTool {
    private static final Log logger = LogFactory.getLog(ResponseTool.class);

    public enum ContentType {
        JSON("application/json"), TEXT("text/plain");
        private String type;

        ContentType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return this.type;
        }
    }

    public static void writeBody(HttpServletResponse response, String body) {
        writeBody(response, body, ContentType.TEXT);
    }

    /**
     * 设置返回的body
     *
     * @param response
     * @param body
     * @param type
     */
    public static void writeBody(HttpServletResponse response, String body, ContentType type) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(type.toString());
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(body);
        } catch (IOException e) {
            logger.debug("Write failed");
        }finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 将map写入返回json
     *
     * @param response
     * @param body
     */
    public static void writeJson(HttpServletResponse response, Map<String, String> body) {
        try {
            writeBody(response, JsonTool.mapToJson(body),ContentType.JSON );
        } catch (IOException e) {
            logger.debug("Convert content to json failed");
        }
    }

    /**
     * 将键值写入返回json
     *
     */
    public static void writeValue(HttpServletResponse response, String key, String value) {
        Map<String, String> body = new HashMap<>(1);
        body.put(key, value);
        writeJson(response, body);
    }
}
