package com.oldbaby.oblib.component.application;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import com.oldbaby.oblib.BuildConfig;
import com.oldbaby.oblib.component.frag.FragBase;
import com.oldbaby.oblib.mvp.view.IMvpView;
import com.oldbaby.oblib.rxjava.RxBus;
import com.oldbaby.oblib.uri.IUriMgr;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.oblib.view.dialog.IConfirmDlgMgr;
import com.oldbaby.oblib.view.dialog.IMultiBtnDlgMgr;
import com.oldbaby.oblib.view.dialog.IProgressDlgMgr;
import com.oldbaby.oblib.view.dialog.IPromptDlgMgr;
import com.oldbaby.oblib.view.dialog.ITipsDlgMgr;

import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * usage: application基类, 使用该库的application类必须从此类继承
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class OGApplication extends MultiDexApplication
        implements UEHandler.ExceptionSender {

    public static final String TAG = "OldBabyApp";
    public static AppConfig APP_CONFIG;
    public static Resources APP_RESOURCE = null;
    public static SharedPreferences APP_PREFERENCE;
    public static Context APP_CONTEXT = null;

    private Handler handler = new Handler();
    private UEHandler ueHandler;
    private static WeakReference<Activity> CUR_ACTIVITY = null;
    private static WeakReference<FragBase> CUR_FRAGMENT = null;
    private int startedActivity = -1;

    private Subscription subscriptionApplicationVisible;

    //region 抽象方法

    /**
     * 配置程序的通用配置
     *
     * @return
     */
    public abstract AppConfig getAppConfig();

    /**
     * 创建通用的确认对话框管理
     *
     * @return
     */
    public abstract IConfirmDlgMgr createConfirmDlgMgr();

    /**
     * 创建通用的加载对话框管理
     *
     * @return
     */
    public abstract IProgressDlgMgr createProgressDlgMgr();

    /**
     * 创建通用的提示框管理
     *
     * @return
     */
    public abstract IPromptDlgMgr createPromptDlgMgr();

    /**
     * 创建通用提示框管理
     */
    public abstract ITipsDlgMgr createTipsDlgMgr();

    /**
     * 创建两个按钮的对话框
     */
    public abstract IMultiBtnDlgMgr createMultiBtnDlgMgr();

    /**
     * 获取uri浏览管理其
     *
     * @return
     */
    public abstract IUriMgr getUriMgr();

    /**
     * 页面可见统计
     */
    public abstract void pageStart(FragBase fragment);


    /**
     * 页面可见统计
     */
    public abstract void pageEnd(FragBase fragment);
    //endregion


    //region application的事件

    // app前台可见
    public void onAppForeGround() {

    }

    // app切到后台
    public void onAppBackGround() {

    }

    //endregion
    //region 生命周期
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        APP_RESOURCE = this.getResources();
        APP_CONTEXT = this;
        APP_CONFIG = getAppConfig();
        // 初始化log配置
        MLog.init(BuildConfig.DEBUG);
        // 初始化WebView 远程调试配置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        subscriptionApplicationVisible = RxBus.getDefault().toObservable(EBAppBackAndFore.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<EBAppBackAndFore>() {
                    @Override
                    public void call(EBAppBackAndFore ebAppBackAndFore) {
                        if (ebAppBackAndFore.getType()
                                == EBAppBackAndFore.TYPE_CUT_FOREGROUND) {
                            onAppForeGround();
                        } else if (ebAppBackAndFore.getType()
                                == EBAppBackAndFore.TYPE_CUT_BACKGROUND) {
                            onAppBackGround();
                        }
                    }
                });

        ueHandler = new UEHandler(this, this);
        // 设置异常处理实例
        Thread.setDefaultUncaughtExceptionHandler(ueHandler);

        createPreference();

        // MobclickAgent.setDebugMode(!StaticWrapper.isRelease());
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
//        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onTerminate() {

        if (subscriptionApplicationVisible != null
                && !subscriptionApplicationVisible.isUnsubscribed()) {
            subscriptionApplicationVisible.unsubscribe();
        }
        super.onTerminate();
    }

    private void createPreference() {
        OGApplication.APP_PREFERENCE = this.getApplicationContext()
                .getSharedPreferences("oldbaby-app", Context.MODE_PRIVATE);
    }
    //endregion


    //region Toast util methods

    /**
     * 从后台线程显示toast
     *
     * @param message
     */
    public static void ShowToastFromBackground(String message) {
        if (APP_CONTEXT instanceof OGApplication) {
            ((OGApplication) APP_CONTEXT).showToast(message);
        }
    }

    private void showToast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(message);
            }
        });
    }
    //endregion

    //region 设置字体不随系统字体大小调整而改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
    //endregion


    //region 设置获取当前的activity
    public static Activity getCurrentActivity() {
        if (CUR_ACTIVITY != null) {
            return CUR_ACTIVITY.get();
        }

        return null;
    }

    public static IMvpView getCurrentMvpView() {
        if (CUR_ACTIVITY != null && CUR_ACTIVITY.get() instanceof IMvpView) {
            return (IMvpView) CUR_ACTIVITY.get();
        }

        return null;
    }

    public static void setCurrentActivity(Activity activity) {

        if (activity != null) {
            MLog.d(TAG, "set to " + activity.getClass().getName());
            CUR_ACTIVITY = new WeakReference<>(activity);
        } else {
            MLog.d(TAG, "set to null");
            CUR_ACTIVITY = null;
        }
    }

    /**
     * 设置当前show的Fragment
     */
    public static void setCurrentFragment(Fragment fragment) {
        if (fragment != null && fragment instanceof FragBase) {
            FragBase curFrag = (FragBase) fragment;
            if (!StringUtil.isNullOrEmpty(curFrag.getPageName())) {
                CUR_FRAGMENT = new WeakReference<>(curFrag);
            }
        } else {
            CUR_FRAGMENT = null;
        }
    }

    /**
     * 获取当前show的Fragment
     */
    public static FragBase getCurrentFragment() {
        if (CUR_FRAGMENT != null) {
            return CUR_FRAGMENT.get();
        }

        return null;
    }

    /**
     * 获取当前的fragment的PageName
     */
    public static String getCurrentFragmentPageName() {
        String pageName = "";
        FragBase currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            pageName = currentFragment.getPageName();
        }
        return pageName;
    }

    //endregion


    //region 记录activity的活动数
    public void startedActivityAdd() {
        if (startedActivity < 0) {
            //初始值为负数，小于0代表应用启动
            startedActivity = 1;
        } else if (startedActivity == 0) {
            //等于0代表从后台到前台。
            startedActivity = 1;
            RxBus.getDefault().post(new EBAppBackAndFore(EBAppBackAndFore.TYPE_CUT_FOREGROUND, null));
        } else {
            startedActivity = startedActivity + 1;
        }
    }

    public void startedActivityCut() {
        startedActivity = startedActivity - 1;
        if (startedActivity == 0) {
            //等于0代表从前台切到后台
            RxBus.getDefault().post(new EBAppBackAndFore(EBAppBackAndFore.TYPE_CUT_BACKGROUND, null));
        }
    }

    public int getStartedActivityCount() {
        return startedActivity;
    }

    /**
     * APP是否在前台
     */
    public boolean isAtFrontDesk() {
        if (startedActivity >= 1) {
            return true;
        } else {
            return false;
        }
    }
    //endregion


}
