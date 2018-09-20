package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/7/13
 * update time:
 * email: 723526676@qq.com
 */

public interface MultiBtnDlgListener<T> {

    // 指定tag的对话框的第一个按钮被点击
    void onMultiBtnOneClicked(Context context, String tag, T arg);
    // 指定tag的对话框的第二个按钮被点击
    void onMultiBtnTwoClicked(Context context, String tag, T arg);
}
