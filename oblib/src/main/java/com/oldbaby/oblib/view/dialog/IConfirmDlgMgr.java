package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * Created by arthur on 2016/9/12.
 */
public interface IConfirmDlgMgr {

    /**
     * 设置事件监听
     *
     * @param listener
     */
    void setListener(IConfirmDlgListener listener);

    /**
     * 展示指定的对话框
     *
     * @param tag        对话框的表示
     * @param title      确认对话框标题
     * @param okText     确认按钮文案
     * @param cancelText 取消按钮文案
     * @param arg        可以附带的参数
     */
    void show(Context context, String tag, String title, String okText, String cancelText, Object arg);

    /**
     * 展示指定的对话框
     *
     * @param tag        对话框的表示
     * @param title      确认对话框标题
     * @param content    对话框描述
     * @param okText     确认按钮文案
     * @param cancelText 取消按钮文案
     * @param arg        可以附带的参数
     */
    void show(Context context, String tag, String title, String content, String okText, String cancelText, Object arg);

    /**
     * 展示指定的对话框
     *
     * @param tag        对话框的表示
     * @param title      确认对话框标题
     * @param content    对话框描述
     * @param okText     确认按钮文案
     * @param cancelText 取消按钮文案
     * @param cancelable 是否可以点击back关闭
     * @param arg        可以附带的参数
     */
    void show(Context context, String tag, String title, String content, String okText, String cancelText, boolean cancelable, Object arg);

    /**
     * 隐藏指定的确认对话框
     *
     * @param tag
     */
    void hide(String tag);
}
