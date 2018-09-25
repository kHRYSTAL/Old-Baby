package com.oldbaby.tracker.util;

import com.oldbaby.oblib.component.frag.FragBase;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
public class TrackerMgr {

    private static class TackerHolder {
        // 静态初始化器，由JVM来保证线程安全
        private static TrackerMgr INSTANCE = new TrackerMgr();
    }

    public static TrackerMgr getInstance() {
        return TackerHolder.INSTANCE;
    }

    /**
     * 从后台切换到前台，和应用启动 两种情况
     */
    public void switchToForeground() {
    }

    /**
     * 从前台切换到后台
     */
    public void switchToBackground() {
    }

    /**
     * 页面start，用于统计
     */
    public void pageStart(FragBase fragment) {
        pageStart(fragment, null);
    }

    /**
     * 页面start，用于统计
     *
     * @param pageInParam 页面来源参数
     */
    public void pageStart(FragBase fragment, String pageInParam) {

    }

    /**
     * 页面stop，用于统计
     */
    public void pageEnd(FragBase fragment) {

    }

    /**
     * @param pageName 页面名字
     * @param alias    点击事件别名
     * @param param    友盟统计参数
     * @param userInfo UC 统计自定义参数
     */
    public void trackerClick(String pageName, String type, String alias, String param, String userInfo) {

    }
}
