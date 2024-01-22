package com.lizumin.wms.tool;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ResponseTool {
    private static final Log logger = LogFactory.getLog(ResponseTool.class);

    /**
     * 将map写入返回json
     *
     * @param response
     * @param body
     */
    public static void writeJson(HttpServletResponse response, Map<String, String> body) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(JsonTool.mapToJson(body));
        } catch (IOException e) {
            logger.debug("Convert content to json failed");
        }finally {
            if (out != null) {
                out.close();
            }
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
