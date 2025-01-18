package me.bytebeats.mns.tool;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtils {
    private static Gson gson = new Gson();

    /**
     * 私有化构造方法
     */
    private GsonUtils() {
    }

    /**
     * 获取单例方法
     */
    private static final Gson getInstance() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String toJson(Object o) {
        return getInstance().toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return getInstance().fromJson(json, tClass);
    }

    public static <T> T fromJson(String json, Type type) {
        return getInstance().fromJson(json, type);
    }

}
