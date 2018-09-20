package com.oldbaby.oblib.view.dialog;

import android.content.Context;

public interface IProgressDlgMgr {

    /**
     * 设置事件监听
     *
     * @param listener
     */
    void setListener(ProgressDlgListener listener);

    /**
     * 展示通用的加载对话框
     *
     * @param content 进度对话框内容
     */
    AProgressDialog show(Context context, String content);

    /**
     * 展示通用的加载对话框
     *
     * @param content 进度对话框内容
     */
    AProgressDialog show(Context context, String content, boolean cancel);

    /**
     * 展示指定的加载对话框
     *
     * @param tag     对话框的表示
     * @param content 进度对话框内容
     */
    AProgressDialog show(Context context, String tag, String content, boolean cancel);

    /**
     * 隐藏指定的加载对话框
     *
     * @param tag
     */
    void hide(String tag);

    /**
     * 隐藏通用的加载对话框
     */
    void hide();
}
