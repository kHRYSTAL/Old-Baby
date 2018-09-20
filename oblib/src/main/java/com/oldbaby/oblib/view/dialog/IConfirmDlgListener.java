package com.oldbaby.oblib.view.dialog;

import android.content.Context;

public interface IConfirmDlgListener {

    //指定tag的对话框的确认按钮被点击
    void onOkClicked(Context context, String tag, Object arg);

    //指定tag的对话框的确认按钮被点击
    void onNoClicked(Context context, String tag, Object arg);
}
