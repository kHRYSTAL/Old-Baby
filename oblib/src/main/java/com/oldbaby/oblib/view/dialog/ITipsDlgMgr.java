package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage: 用于提示的dialog 只有右上角有关闭按钮
 * author: kHRYSTAL
 * create time: 17/7/12
 * update time:
 * email: 723526676@qq.com
 */

public interface ITipsDlgMgr {
    /**
     * 展示指定的对话框
     *
     * @param tag           对话框的表示
     * @param listener
     */
    void show(Context context, String tag, TipsDlgAttr tipsDlgAttr, TipsDlgListener listener);

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
