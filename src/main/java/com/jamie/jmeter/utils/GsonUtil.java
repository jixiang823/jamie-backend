package com.jamie.jmeter.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Gson工具类
 */
public class GsonUtil {

    private static Gson gson = new Gson();

    public GsonUtil() {}

    /**
     * Object => Json
     *
     * @param object 对象
     * @return Json字符串
     */
    public static String objToJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * Json => Bean
     *
     * @param json Json字符串
     * @param cls Bean类
     * @return Bean对象
     */
    public static <T> T jsonToBean(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    /**
     * Json => List
     *
     * @param json Json字符串
     * @return List
     */
    public static <T> List<T> jsonToList(String json) {
        return gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
    }

    /**
     * Json => ListMap
     *
     * @param json Json字符串
     * @return 包含Map的List
     */
    public static <T> List<Map<String, T>> jsonToListMap(String json) {
        return gson.fromJson(json, new TypeToken<List<Map<String, T>>>(){}.getType());
    }

    /**
     * Json => Map
     *
     * @param json Json字符串
     * @return Map
     */
    public static <T> Map<String, T> jsonToMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, T>>(){}.getType());
    }

    /**
     * 输出格式化的json字符串
     * @param json 未格式化的json字符串
     * @return
     */
    public static String toPrettyFormat(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }


}
