package com.example.jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @author zhanghongqiang
 * @date 2019-06-10 11:21
 */
public class JsonUtil {
    private static Gson gson = null;

    public static JsonUtil instance = null;

    private JsonUtil() {
    }

    public static JsonUtil getInstance() {
        if (null == instance) {
            synchronized (JsonUtil.class) {
                instance = new JsonUtil();
                gson = new GsonBuilder().create();
            }
        }
        return instance;
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * 解析Json数据
     *
     * @param typetoken Gson的类型
     * @return 返回数据数组
     */
    public <T> T parser(String json, TypeToken<T> typetoken) {
        return gson.fromJson(json, typetoken.getType());
    }

    /**
     * 解析Json数据
     *
     * @param typetoken Gson的类型
     * @return 返回数据数组
     */
    public <T> List<T> parserList(String jsonArray, TypeToken<List<T>> typetoken) {
        return gson.fromJson(jsonArray, typetoken.getType());
    }

    /**
     * 解析Json数据，返回实例
     *
     * @param json
     * @param classOfT
     * @return
     */
    public <T> T parser(String json, Class<T> classOfT) {
        if (json != null) {
            return gson.fromJson(json, classOfT);
        }
        return null;
    }
}
