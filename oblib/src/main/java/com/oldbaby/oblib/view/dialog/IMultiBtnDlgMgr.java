package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/7/13
 * update time:
 * email: 723526676@qq.com
 */

public interface IMultiBtnDlgMgr {

    /**
     * 展示指定的对话框
     *
     * @param tag           对话框的表示
     * @param multiBtnDlgAttr
     * @param listener
     */
    void show(Context context, String tag, MultiBtnDlgAttr multiBtnDlgAttr, MultiBtnDlgListener listener);

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
