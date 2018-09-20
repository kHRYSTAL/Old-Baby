package com.oldbaby.oblib.view.dialog;

import android.content.Context;

public interface ProgressDlgListener {

    //指定进度对话框Cancel
    void onCancel(Context context, String tag);

    //指定进度对话框Dismiss
    void onDismiss(Context context, String tag);
}
