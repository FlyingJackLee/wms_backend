package com.lizumin.wms.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Map;

public class JsonTool{
    /**
     * 将对象转换为json
     *
     * @param target
     * @return  target = null: 返回 "null"字符串
     *          target = ""： 返回 "\"\""字符串
     * @throws JsonProcessingException
     */
    public static String objToJson(Serializable target) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(target);
    }

    public static <T> T jsonToObj(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, clazz);
    }

    public static String mapToJson(Map<String, String> target) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(target);
    }
}
