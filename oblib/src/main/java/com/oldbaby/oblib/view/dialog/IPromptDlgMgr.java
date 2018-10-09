package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/11/9
 * update time:
 * email: 723526676@qq.com
 */

public interface IPromptDlgMgr {

    /**
     * 展示指定的对话框
     *
     * @param tag           对话框的表示
     * @param promptDlgAttr
     * @param listener
     */
    void show(Context context, String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener);

    void show(Context context, String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener, PromptDlgTwoBtnListener twoBtnListener);


    /**
     * 隐藏指定的确认对话框
     *
     * @param tag
     */
    void hide(String tag);

    /**
     * 指定的对话框是否正在显示
     */
    boolean isShowing(String tag);
}
