package com.oldbaby.common.base;

import android.app.Activity;
import android.view.View;

import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.title.OnTitleClickListener;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class DefaultTitleBarClickListener implements OnTitleClickListener {

    private Activity activity;

    public DefaultTitleBarClickListener(Activity activity) {
        this.activity = activity;
    }

    /**
     * 点击回退
     */
    public void onBack() {
        activity.onBackPressed();
    }

    @Override
    public void onTitleClicked(View view, int tagId) {
        try {
            switch (tagId) {
                case TitleBarProxy.TAG_BACK:
                    onBack();
                    break;
                default:
                    break;
            }
        } catch (final Throwable e) {
            MLog.e("FragBaseActivity", "exception happened", e);
        }
    }
}
