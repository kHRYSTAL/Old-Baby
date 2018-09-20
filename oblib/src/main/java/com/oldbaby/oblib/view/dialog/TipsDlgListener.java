package com.oldbaby.oblib.view.dialog;

import android.content.Context;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 17/7/12
 * update time:
 * email: 723526676@qq.com
 */

public interface TipsDlgListener<T> {
    public void onTipsCloseClick(Context context, String tag, T args);
}
