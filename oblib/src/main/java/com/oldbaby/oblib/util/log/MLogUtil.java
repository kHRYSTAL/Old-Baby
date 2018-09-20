package com.oldbaby.oblib.util.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/1/5
 * update time:
 * email: 723526676@qq.com
 */

public class MLogUtil {

    /**
     * 判断某行是否为空
     * @param line
     * @return
     */
    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    /**
     * 在首行和末尾行插入标识 用于json打印
     * @param tag
     * @param isTop
     */
    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }


}
