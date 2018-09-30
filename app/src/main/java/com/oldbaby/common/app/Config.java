package com.oldbaby.common.app;

import com.oldbaby.oblib.component.application.EnvType;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class Config {

    private static final String TAG = "config";
    public static int ENV_TYPE = EnvType.ENV_DEV;

    public static String getRetrofitBaseUrl() {
        switch (ENV_TYPE) {
            case EnvType.ENV_DEV:
            case EnvType.ENV_TEST:
            case EnvType.ENV_TEST_INTERNET:
            case EnvType.ENV_RELEASE:
                return String.format("http://%s", getHost());
            default:
                return null;
        }
    }

    private static String getHost() {
        switch (ENV_TYPE) {
            case EnvType.ENV_TEST:
            case EnvType.ENV_DEV:
            case EnvType.ENV_TEST_INTERNET:
            case EnvType.ENV_RELEASE:
                return "140.143.240.200";
            default:
                return null;
        }
    }
}
