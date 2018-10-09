package com.oldbaby.oblib.view.dialog;

import android.content.Context;

public interface PromptDlgTwoBtnListener<T> {

    //指定tag的对话框的左按钮被点击
    void onPromptLeftClicked(Context context, String tag, T arg);

    //指定tag的对话框的右按钮被点击
    void onPromptRightClicked(Context context, String tag, T arg);

}