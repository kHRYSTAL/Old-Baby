package com.oldbaby.oblib.view.pulltorefresh;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.oldbaby.oblib.view.pulltorefresh.cache.PullToRefreshCache;

public class PullToRefreshProxy<V extends View> implements
        OnRefreshListener2<V> {

    private static final long REFRESH_DELAY = 700;

    protected PullToRefreshBase<? extends V> pullView;
    protected PullToRefreshCache pullCache;
    protected PullRefeshListener pullListener;
    protected LinearLayout headerContainer;
    protected LinearLayout footerContainer;

    /**
     * 当前下拉刷新的状态
     */
    protected PullEvent currentEvent = PullEvent.none;

    protected Handler handler = new Handler();
    protected boolean isFirstResume = true;

    public Boolean needRefreshOnResume = true;

    public PullToRefreshProxy() {

    }

    // =========生命周期方法开始=======

    /**
     * 该方法会在activity或者Fragment的OnCreate事件中调用
     */
    public void onCreate() {
    }

    /**
     * 该方法会在activity或者Fragment的OnResume事件中调用， 建议在这里进行缓存数据的加载以及自动刷新逻辑
     */
    public void onResume() {
        if (isFirstResume && needRefreshOnResume) {
            isFirstResume = false;
            if (pullCache == null || shouldRefreshWhenStart()) {
                this.currentEvent = PullEvent.none;
                handler.postDelayed(refreshRunable, REFRESH_DELAY);
            }
        }
    }

    /**
     * 判断当proxy启动的时候是否应该自动刷新
     *
     * @return
     */
    private boolean shouldRefreshWhenStart() {
        return pullCache.isOutRefreshInterval();
    }

    private Runnable refreshRunable = new Runnable() {

        @Override
        public void run() {
            pullView.setRefreshing();
        }
    };

    /**
     * this maybe invoked multi-time, when you want to reuse an internal view,
     * you can invoke this to reset it
     */
    public void onDestroy() {
        handler.removeCallbacks(refreshRunable);
        this.onRefreshFinished();
    }

    // =========生命周期方法结束=======

    // =============refresh 实现开始============

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<V> refreshView) {
        if (this.isRefreshing() == false) {
            this.currentEvent = PullEvent.normal;
            pullListener.loadNormal();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<V> refreshView) {
        if (this.isRefreshing() == false) {
            this.currentEvent = PullEvent.more;
            pullListener.loadMore(null);
        }
    }

    // =============refresh 实现结束===================

    /**
     * 设置刷新view，并监听view的刷新事件
     *
     * @param pullView
     */
    public void setPullView(PullToRefreshBase<? extends V> pullView) {
        this.pullView = pullView;
        this.pullView.setOnRefreshListener((OnRefreshListener2) this);
    }

    /**
     * 只有设置这个key以后，自动缓存、自动刷新等才会启用
     *
     * @param refreshKey
     */
    public void setRefreshKey(String refreshKey) {
        if (pullCache != null) {
            throw new RuntimeException("PullToRefreshProxy 不允许重复设置refreshKey");
        }
        this.pullCache = new PullToRefreshCache(refreshKey);
    }

    /**
     * 设置拉动刷新监听器，必须在onResume之前进行设定
     *
     * @param pullListener
     */
    public void setPullListener(PullRefeshListener pullListener) {
        this.pullListener = pullListener;
    }

    /**
     * 获取当前proxy代理的刷新view
     *
     * @return
     */
    public PullToRefreshBase<? extends V> getPullView() {
        return pullView;
    }

    /**
     * 获取刷新的cache类，可以通过这个对象设置刷新的一部分参数
     *
     * @return
     */
    public PullToRefreshCache getPullCache() {
        return pullCache;
    }

    /**
     * 获取当前的刷新状态
     *
     * @return
     */
    protected PullEvent getCurrentEvent() {
        return this.currentEvent;
    }

    /**
     * 设置当前的刷新状态
     */
    public void setCurrentEvent(PullEvent event) {
        this.currentEvent = event;
    }

    /**
     * 结束刷新，并重置刷新状态
     */
    public void onRefreshFinished() {
        this.pullView.onRefreshComplete();
        this.currentEvent = PullEvent.none;
    }

    /**
     * 判断是否在进行刷新
     *
     * @return
     */
    protected boolean isRefreshing() {
        return (this.currentEvent != PullEvent.none);
    }

    /**
     * 如果当前没有刷新在进行，则进行刷新操作
     *
     * @param showRefreshingHeader true:显示刷新view， false:不现实刷新view
     */
    public void pullDownToRefresh(boolean showRefreshingHeader) {
        if (!this.isRefreshing()) {
            pullView.resetCurrentMode();
            pullView.setRefreshing(showRefreshingHeader);
        }
    }

    public void addHeader(View v) {
        if (headerContainer != null) {
            headerContainer.addView(v);
        }
    }

    public void addFooter(View v) {
        if (footerContainer != null) {
            footerContainer.addView(v);
        }
    }

    public void removeHeader(View v) {
        if (headerContainer != null) {
            headerContainer.removeView(v);
        }
    }

    public void removeFooter(View v) {
        if (footerContainer != null) {
            footerContainer.removeView(v);
        }
    }

    protected void setHeaderAndFooter(Context context, ListView listView) {
        headerContainer = new LinearLayout(context);
        footerContainer = new LinearLayout(context);
        headerContainer.setOrientation(LinearLayout.VERTICAL);
        footerContainer.setOrientation(LinearLayout.VERTICAL);

        /**
         * here is fix cannot pull up refresh, which maybe listview's bug, when
         * listview's footer's height is 0, the last visible position will never
         * be footer, so pull refresh think listview has more content to
         * display, not calling pulling up to refresh
         */
        footerContainer.setPadding(0, 1, 0, 0); // maybe
        listView.addHeaderView(headerContainer);
        listView.addFooterView(footerContainer);
    }
}
