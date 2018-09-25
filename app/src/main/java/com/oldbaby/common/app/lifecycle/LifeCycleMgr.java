package com.oldbaby.common.app.lifecycle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.oldbaby.oblib.component.act.BaseFragmentActivity;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.MLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
public class LifeCycleMgr implements Application.ActivityLifecycleCallbacks {

    /**
     * 可用的上下文, 在start和stop事件中管理使用, 比如启动相机后, 应用被销毁
     * 照相完成后 没有走activity的onResume, 所以currentActivity事件不能拿到可用的Activity
     */
    private List<WeakReference<Activity>> availibleActivityStack;

    public LifeCycleMgr() {
        availibleActivityStack = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        MLog.d(OGApplication.TAG, "created: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        MLog.d(OGApplication.TAG, "started: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        MLog.d(OGApplication.TAG, "resumed: " + activity.getClass().getSimpleName());
        OGApplication.setCurrentActivity(activity);
        WeakReference<Activity> actWeak = new WeakReference<>(activity);
        availibleActivityStack.add(0, actWeak);
        // TODO: 18/9/25  需要增加踢出登录逻辑
    }

    @Override
    public void onActivityPaused(Activity activity) {
        MLog.d(OGApplication.TAG, "paused: " + activity.getClass().getSimpleName());
        OGApplication.setCurrentActivity(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        MLog.d(OGApplication.TAG, "stopped: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        MLog.d(OGApplication.TAG, "onActivitySaveInstanceState: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        MLog.d(OGApplication.TAG, "destroy: " + activity.getClass().getSimpleName());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public BaseFragmentActivity getLastAvailibleActivity() {
        if (availibleActivityStack.isEmpty())
            return null;
        BaseFragmentActivity resultActivity = null;
        for (WeakReference<Activity> weakAct : availibleActivityStack) {
            if (weakAct != null && weakAct.get() != null && weakAct.get() instanceof  BaseFragmentActivity) {
                BaseFragmentActivity activity = (BaseFragmentActivity) weakAct.get();
                if (!activity.isDestroyed()) {
                    resultActivity = activity;
                    break;
                }
            }
        }
        return resultActivity;
    }
}
