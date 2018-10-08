package com.oldbaby.common.util;

import com.bumptech.glide.load.model.LazyHeaders;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public class SpiderHeader {

    private static SpiderHeader instance = null;
    private final LazyHeaders.Builder builder;

    private SpiderHeader() {
        builder = new LazyHeaders.Builder();
    }

    public static SpiderHeader getInstance() {
        if (instance == null) {
            synchronized (SpiderHeader.class) {
                if (instance == null) {
                    instance = new SpiderHeader();
                }
            }
        }
        return instance;
    }

    public SpiderHeader addHeader(String key, String value) {
        builder.addHeader(key, value);
        return instance;
    }

    public SpiderHeader addRefer(String value) {
        builder.addHeader("referer", value);
        return instance;
    }

    public SpiderHeader addUserAgent() {
        builder.addHeader("user-agent", "Mozilla/5.0 (android) GoogleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36");
        return instance;
    }

    public SpiderHeader addInsecureRequests() {
        builder.addHeader("upgrade-insecure-requests", String.valueOf(1));
        return instance;
    }

    public LazyHeaders build() {
        return builder.build();
    }
}
