package com.oldbaby.common.app;

import android.content.Context;

import com.oldbaby.common.app.lifecycle.LifeCycleMgr;
import com.oldbaby.common.dto.DBMgr;
import com.oldbaby.oblib.component.application.AppConfig;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.component.frag.FragBase;
import com.oldbaby.oblib.uri.IUriMgr;
import com.oldbaby.oblib.view.dialog.IConfirmDlgMgr;
import com.oldbaby.oblib.view.dialog.IMultiBtnDlgMgr;
import com.oldbaby.oblib.view.dialog.IProgressDlgMgr;
import com.oldbaby.oblib.view.dialog.IPromptDlgMgr;
import com.oldbaby.oblib.view.dialog.ITipsDlgMgr;
import com.oldbaby.oblib.view.pulltorefresh.cache.IPullCache;
import com.oldbaby.oblib.view.pulltorefresh.cache.PullToRefreshCache;
import com.oldbaby.tracker.util.TrackerMgr;

import java.io.Serializable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
public class OldBabyApplication extends OGApplication {

    private LifeCycleMgr lifeCycleMgr = new LifeCycleMgr();

    public static void trackerClickEvent(String pageName, String type, String alias) {
        if (APP_CONTEXT instanceof  OldBabyApplication) {
            ((OldBabyApplication) APP_CONTEXT).trackerClick(pageName, type, alias, null, null);
        }
    }

    public static void trackerClickEvent(String pageName, String type, String alias, String param) {
        if (APP_CONTEXT instanceof  OldBabyApplication) {
            ((OldBabyApplication) APP_CONTEXT).trackerClick(pageName, type, alias, param, null);
        }
    }

    public static void trackerClickEvent(String pageName, String type, String alias, String param, String userInfo) {
        if (APP_CONTEXT instanceof  OldBabyApplication) {
            ((OldBabyApplication) APP_CONTEXT).trackerClick(pageName, type, alias, param, userInfo);
        }
    }

    private void trackerClick(String pageName, String type, String alias, String param, String userInfo) {
        // TODO: 18/9/25 TrackerMgr;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // TODO: 18/9/25 Tinker or zenus install
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configTinker();
        configRetrofit();
        configPullToRefresh();
        registerActivityLifecycleCallbacks(lifeCycleMgr);
        registerActivityLifecycleCallbacks(new TrackerLifeCycleMgr(OldBabyApplication.this));
        // TODO GsonHelper
    }

    private void configPullToRefresh() {
        PullToRefreshCache.setCacheUtil(new IPullCache() {

            @Override
            public Object getCache(String cacheKey) {
                return DBMgr.getMgr().getCacheDao().get(cacheKey);
            }

            @Override
            public void cacheData(String cacheKey, Serializable data) {
                DBMgr.getMgr().getCacheDao().set(cacheKey, data);
            }
        });
    }

    private void configRetrofit() {

    }

    private void configTinker() {

    }

    @Override
    public void onAppForeGround() {
        super.onAppForeGround();
    }

    @Override
    public void onAppBackGround() {
        super.onAppBackGround();
    }

    @Override
    public void startedActivityAdd() {
        super.startedActivityAdd();
        if (getStartedActivityCount() == 1) {
            //从后台切换到前台，和应用启动 两种情况
            TrackerMgr.getInstance().switchToForeground();
            switchFrontOrBackDesk("awaken");
        }
    }

    @Override
    public void startedActivityCut() {
        super.startedActivityCut();
        if (getStartedActivityCount() == 0) {
            //从前台切换到后台
            TrackerMgr.getInstance().switchToBackground();
            switchFrontOrBackDesk("background");
        }
    }

    @Override
    public void pageStart(FragBase fragment) {
        TrackerMgr.getInstance().pageStart(fragment);
    }

    @Override
    public void pageEnd(FragBase fragment) {
        TrackerMgr.getInstance().pageEnd(fragment);
    }


    @Override
    public AppConfig getAppConfig() {
        AppConfig config = new AppConfig();
        config.setRootDir("oldbabyapp");
        config.setSchema("oldbaby");
        return config;
    }

    @Override
    public IConfirmDlgMgr createConfirmDlgMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public IProgressDlgMgr createProgressDlgMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public IPromptDlgMgr createPromptDlgMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public ITipsDlgMgr createTipsDlgMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public IMultiBtnDlgMgr createMultiBtnDlgMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public IUriMgr getUriMgr() {
        // TODO: 18/9/25
        return null;
    }

    @Override
    public void sendException(String exception) {

    }

    /**
     * 前台切换后台 或后台切换前台 需要告知服务器
     * @param type
     */
    private void switchFrontOrBackDesk(String type) {
        // TODO: 18/9/25
    }
}
