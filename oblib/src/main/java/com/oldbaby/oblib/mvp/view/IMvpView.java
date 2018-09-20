package com.oldbaby.oblib.mvp.view;

import com.oldbaby.oblib.uri.UriParam;
import com.oldbaby.oblib.view.dialog.MultiBtnDlgAttr;
import com.oldbaby.oblib.view.dialog.MultiBtnDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgAttr;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.TipsDlgAttr;
import com.oldbaby.oblib.view.dialog.TipsDlgListener;

import java.util.List;

/**
 * usage: 所有View视图的基类
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IMvpView {

    public static final String TAG_CONFIRM_DLG_SYSTEM_PERMISSION = "tag_confirm_dlg_system_permission";
    public static final String DLG_CONTENT_SDCARD_PERMISSION = "你关闭了手机存储权限\n请到设置中开启再试";
    public static final String DLG_CONTENT_CAMERA_PERMISSION = "你关闭了相机访问权限\n请到设置中开启再试";

    /**
     * 显示loading dialog
     */
    void showProgressDlg();

    /**
     * 显示loading dialog
     *
     * @param content loading 内容
     */
    void showProgressDlg(String content);

    /**
     * 显示loading dialog
     *
     * @param content loading 内容
     * @param cancel  点击对话框以外是否隐藏
     */
    void showProgressDlg(String content, boolean cancel);

    /**
     * 显示loading dialog
     *
     * @param tag     对话框标识
     * @param content loading 内容
     */
    void showProgressDlg(String tag, String content);

    /**
     * 显示loading dialog
     *
     * @param tag     对话框标识
     * @param content loading 内容
     * @param cancel  点击对话框以外是否隐藏
     */
    void showProgressDlg(String tag, String content, boolean cancel);

    /**
     * 隐藏loading dialog
     */
    void hideProgressDlg();

    /**
     * 隐藏loading dialog
     */
    void hideProgressDlg(String tag);

    /**
     * 显示toast
     */
    void showToast(String toast);

    /**
     * 跳转到指定的URI
     *
     * @param uri 页面uri或者path
     */
    void gotoUri(String uri);

    /**
     * 跳转到指定的URI
     *
     * @param uri     页面uri或者path
     * @param param   参数
     */
    void gotoUri(String uri, UriParam param);

    /**
     * 跳转到指定的URI
     *
     * @param uri     页面uri或者path
     * @param params  参数list
     */
    void gotoUri(String uri, List<UriParam> params);

    /**
     * 展示确认对话框
     *
     * @param tag
     * @param title
     * @param okText
     * @param noText
     * @param arg    可以为空
     */
    void showConfirmDlg(String tag, String title, String okText, String noText, Object arg);

    /**
     * 展示确认对话框
     *
     * @param tag
     * @param title
     * @param content
     * @param okText
     * @param noText
     * @param arg
     */
    void showConfirmDlg(String tag, String title, String content, String okText, String noText, Object arg);

    /**
     * 展示确认对话框
     *
     * @param tag
     * @param title
     * @param content
     * @param okText
     * @param noText
     * @param cancelable
     * @param arg
     */
    void showConfirmDlg(String tag, String title, String content, String okText, String noText, boolean cancelable, Object arg);

    /**
     * 隐藏确认对话框
     */
    void hideConfirmDlg(String tag);

    /**
     * 关闭自己
     */
    void finishSelf();

    //获取页面名称
    String getPageName();

    /**
     * 显示提示框
     *
     * @param tag
     * @param promptDlgAttr
     * @param listener
     */
    void showPromptDlg(String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener);

    /**
     * 隐藏提示框
     *
     * @param tag
     */
    void hidePromptDlg(String tag);

    /**
     * 指定的对话框是否正在显示
     */
    boolean isPromptDlgShowing(String tag);

    /**
     * 显示提示框
     */
    void showTipsDlg(String tag, TipsDlgAttr tipsDlgAttr, TipsDlgListener listener);

    /**
     * 隐藏提示框
     */
    void hideTipsDlg(String tag);

    /**
     * 指定的提示框是否正在显示
     */
    boolean isTipsDlgShowing(String tag);

    /**
     * 显示多按钮对话框
     */
    void showMultiBtnDlg(String tag, MultiBtnDlgAttr multiBtnDlgAttr, MultiBtnDlgListener listener);

    /**
     * 隐藏多按钮对话框
     */
    void hideMultiBtnDlg(String tag);

    /**
     * 判断多按钮对话框是否正在显示
     */
    boolean isMultiBtnDlgShowing(String tag);

    /**
     * 系统权限引起的提示，permissionPrompt 为具体的提示文案，"去设置"和"取消"按钮，点击"去设置"跳转到系统设置页面
     */
    void showSystemPermissionDlg(String permissionPrompt);

    /**
     * 跳转到系统设置页面
     */
    void goToSysSetting();

}
