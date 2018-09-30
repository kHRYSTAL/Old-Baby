package com.oldbaby.common.retrofit;

import android.app.Activity;

import com.oldbaby.common.app.OldBabyApplication;
import com.oldbaby.oblib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class ApiErrorHandler {
    private ArrayList<Integer> noPromptCodes;
    private List<Integer> aaCode;

    private ApiErrorHandler() {
        aaCode = new ArrayList<>();
        // TODO: 18/9/29 用户提出错误码
        noPromptCodes = new ArrayList<>();
        // TODO: 18/9/29 不需要前端提示的错误码
    }

    /**
     * 处理通用的API接口错误
     *
     * @param code
     * @param message
     * @return
     */
    public boolean handleApiError(int code, String message) {

        if (isAaError(code)) {
            // TODO: 18/9/29 用户踢出逻辑

            return true;
        } else if (shouldShowPrompt(code)
                && !StringUtil.isNullOrEmpty(message)) {
            OldBabyApplication.ShowToastFromBackground(message);
        }
        return false;
    }

    private boolean isAaError(int code) {
        return aaCode.contains(code);
    }

    private boolean shouldShowPrompt(int code) {
        return !noPromptCodes.contains(code);
    }

    public static ApiErrorHandler INSTANCE() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static ApiErrorHandler INSTANCE = new ApiErrorHandler();
    }
}
