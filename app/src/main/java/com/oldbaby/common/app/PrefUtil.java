package com.oldbaby.common.app;

import com.tencent.mmkv.MMKV;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class PrefUtil {

    private static PrefUtil instance;

    public static final PrefUtil Instance() {
        if (instance == null) {
            synchronized (PrefUtil.class) {
                if (instance == null)
                    instance = new PrefUtil();
            }
        }
        return instance;
    }

    private PrefUtil() {
    }

    private static final String PREF_TOKEN = "pref_token";
    private static final String PREF_UID = "pref_uid";
    private static final String ZOOM_PAGE_TEXT_SIZE = "zoom_page_text_size";
    private static final String ZOOM_PAGE_TEXT_RATIO = "zoom_page_text_ratio";

    public void setToken(String token) {
        MMKV.defaultMMKV().encode(PREF_TOKEN, token);
    }

    public String getToken() {
        String s = MMKV.defaultMMKV().decodeString(PREF_TOKEN, null);
        return s;
    }

    public long getUserId() {
        return MMKV.defaultMMKV().decodeLong(PREF_UID, 0L);
    }

    public void setUserID(long i) {
        MMKV.defaultMMKV().encode(PREF_UID, i);
    }

    public void setZoomPageTextSize(float textSize) {
        MMKV.defaultMMKV().encode(ZOOM_PAGE_TEXT_SIZE, textSize);
    }

    public float getZoomPageTextSize() {
        return MMKV.defaultMMKV().decodeFloat(ZOOM_PAGE_TEXT_SIZE, 20.0f);
    }

    public void setZoomPageTextRatio(float ratio) {
        MMKV.defaultMMKV().encode(ZOOM_PAGE_TEXT_RATIO, ratio);
    }

    public float getZoomPageTextRatio() {
        return MMKV.defaultMMKV().decodeFloat(ZOOM_PAGE_TEXT_RATIO, 1.0f);
    }

    public void clearAll() {
        MMKV.defaultMMKV().removeValuesForKeys(new String[]{
                PREF_TOKEN,
                PREF_UID,
                ZOOM_PAGE_TEXT_RATIO,
                ZOOM_PAGE_TEXT_SIZE
        });
    }
}
