package com.oldbaby.oblib.util.log;

import android.util.Log;

import com.oldbaby.oblib.util.MLog;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/1/5
 * update time:
 * email: 723526676@qq.com
 */

public class BaseLog {

    private static final int MAX_LENGTH = 4000;

    /**
     * 解决日志一行长度超过4000 打印不全问题
     * @param type
     * @param tag
     * @param msg
     */
    public static void printDefault(int type, String tag, String msg) {
        int index = 0;
        int length = msg.length();
        int countOfSub = length / MAX_LENGTH;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + MAX_LENGTH);
                printSub(type, tag, sub);
                index += MAX_LENGTH;
            }
            printSub(type, tag, msg.substring(index, length));
        } else {
            printSub(type, tag, msg);
        }
    }

    /**
     * 执行实际的Log 输出
     * @param type
     * @param tag
     * @param sub
     */
    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case MLog.V:
                Log.v(tag, sub);
                break;
            case MLog.D:
                Log.d(tag, sub);
                break;
            case MLog.I:
                Log.i(tag, sub);
                break;
            case MLog.W:
                Log.w(tag, sub);
                break;
            case MLog.E:
                Log.e(tag, sub);
                break;
            case MLog.A:
                Log.wtf(tag, sub);
                break;
        }
    }

}
