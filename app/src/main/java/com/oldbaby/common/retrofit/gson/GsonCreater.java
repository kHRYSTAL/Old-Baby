package com.oldbaby.common.retrofit.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oldbaby.oblib.util.gson.DateDeserializer;
import com.oldbaby.oblib.util.gson.GsonExclusionStrategy;

import java.util.Date;

/**
 * usage: 将服务器字段dto至客户端字段构造器, 用于在接口改动或升级时 不可避免的兼容性适配
 *  目前只用于解决使用gson序列化出现date无法转化问题
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class GsonCreater {

    public static Gson GreateGson() {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .setExclusionStrategies(new GsonExclusionStrategy())
                .serializeSpecialFloatingPointValues();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        return gsonBuilder.create();
    }
}
