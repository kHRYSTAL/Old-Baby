package com.oldbaby.oblib.util.log;

import android.util.Log;

import com.oldbaby.oblib.util.MLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * usage: 用于Json打印
 * author: kHRYSTAL
 * create time: 17/1/5
 * update time:
 * email: 723526676@qq.com
 */

public class JsonLog {

    /**
     * 输出json日志 通过插入缩进判断何时换行
     *
     * @param tag
     * @param msg
     * @param headString
     */
    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                // 每个层级叠加4个空格的缩进 则认为是linux换行
                message = jsonObject.toString(MLog.JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(MLog.JSON_INDENT);
            } else {
                // 非json
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        MLogUtil.printLine(tag, true);
        message = headString + MLog.LINE_SEPARATOR + message;
        String[] lines = message.split(MLog.LINE_SEPARATOR);
        for (String line : lines) {
            Log.e(tag, "║ " + line);
        }
        MLogUtil.printLine(tag, false);
    }
}
