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
    private final LazyHeaders headers;

    private SpiderHeader() {
        headers = new LazyHeaders.Builder().addHeader("User-Agent",
                "Mozilla/5.0 (android) GoogleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36").build();
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

    public LazyHeaders getHeaders() {
        return headers;
    }
}
