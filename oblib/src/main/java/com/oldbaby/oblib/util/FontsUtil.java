package com.oldbaby.oblib.util;

import android.graphics.Typeface;

import com.oldbaby.oblib.component.application.OGApplication;

/**
 * 字体工具类
 */
public class FontsUtil {

    public static final String PATH_SONG_TI = "fonts/SongTi.ttf"; // 宋体路径

    private Typeface songTiType;

    /**
     * 获取宋体type
     */
    public Typeface getSongTiTypeFace() {
        if (songTiType == null) {
            // 将字体文件保存在assets/fonts/目录下，创建Typeface对象
            songTiType = Typeface.createFromAsset(OGApplication.APP_CONTEXT.getAssets(), PATH_SONG_TI);
        }
        return songTiType;
    }

    public static FontsUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static FontsUtil INSTANCE = new FontsUtil();
    }

    private FontsUtil() {

    }
}
