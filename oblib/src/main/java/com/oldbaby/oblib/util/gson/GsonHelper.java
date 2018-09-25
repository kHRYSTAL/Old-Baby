package com.oldbaby.oblib.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonHelper {

    private static final String TIME_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            TIME_FORMAT, Locale.US);

    private static final Object LockObj = new Object();
    private static Gson commGson;
    private static Gson pureGson;

    public static Gson GetPureGson() {
        if (pureGson == null) {
            synchronized (LockObj) {
                if (pureGson == null) {
                    pureGson = new Gson();
                }
            }
        }
        return pureGson;
    }

    /**
     * 加上了GsonExclude标注的变量，都不会被序列化
     * Date DateDeserialize是控制时间日期的显示格式
     *
     * @return
     */
    public static Gson GetCommonGson() {
        if (commGson == null) {
            synchronized (LockObj) {
                if (commGson == null) {
                    GsonBuilder gsonBuilder = new GsonBuilder()
                            .setExclusionStrategies(new GsonExclusionStrategy())
                            .serializeSpecialFloatingPointValues();
                    gsonBuilder.registerTypeAdapter(Date.class,
                            new DateDeserializer());

                    commGson = gsonBuilder.create();
                }
            }
        }
        return commGson;
    }

    public static void SetCommonGson(Gson gson) {
        commGson = gson;
    }

}