package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/11/9
 * update time:
 * email: 723526676@qq.com
 */

public interface PromptDlgListener<T> {

    //指定tag的对话框的按钮被点击
    void onPromptClicked(Context context, String tag, T arg);

}