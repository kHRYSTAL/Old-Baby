package com.oldbaby.common.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.MLog;

/**
 * usage: 记录前后台切换lifecycle
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
public class TrackerLifeCycleMgr implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = TrackerLifeCycleMgr.class.getSimpleName();
    private OGApplication application;
    private int count;

    public TrackerLifeCycleMgr(OGApplication application) {
        this.application = application;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        MLog.v(TAG, activity + "onActivityStarted");
        if (application != null)
            application.startedActivityAdd();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        MLog.v(TAG, activity + "onActivityStopped");
        if (application != null)
            application.startedActivityCut();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
