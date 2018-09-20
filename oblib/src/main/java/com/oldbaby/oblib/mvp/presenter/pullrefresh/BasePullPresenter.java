package com.oldbaby.oblib.mvp.presenter.pullrefresh;

import com.oldbaby.oblib.component.lifeprovider.PresenterEvent;
import com.oldbaby.oblib.mvp.model.pullrefresh.IPullMode;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.pullrefresh.IPullView;
import com.oldbaby.oblib.mvp.view.pullrefresh.LogicIdentifiable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * usage: 列表页面的presenter
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class BasePullPresenter<D extends LogicIdentifiable, M extends IPullMode<D>, V extends IPullView<D>>
        extends BasePresenter<M, V> {

    //第一次执行updateView 的标志位。
    boolean isFirstCreate = true;
    //第一次执行onViewResume的标志位
    boolean isFirstResume = true;
    //延迟刷新的订阅
    private Subscription refreshDelayOb;

    @Override
    protected void updateView() {
        super.updateView();
        if (isFirstCreate && setupDone()) {
            isFirstCreate = false;
            view().appendItems(getCache());
        }
    }

    /**
     * 页面onResume。根据条件判断是否要自动刷新
     */
    public void onViewResume() {
        if (isFirstResume) {
            isFirstResume = false;
            if (firstAutoRefresh()) {
                refreshDelay();
            }
        } else if (shouldAutoRefresh()) {
            refreshDelay();
        }
    }

    /**
     * 获取自动刷新间隔时间，默认为30分钟，重写该方法可改变。
     */
    protected long getAutoRefreshInterval() {
        return -1;
    }

    /**
     * 页面创建后是否自动刷新一次。默认为刷新，重写该方法可改变
     */
    protected boolean firstAutoRefresh() {
        return true;
    }

    /**
     * 页面下拉刷新。加载数据，并记录该次刷新时间。上层最好不要调用，或者需要自己处理maxId，和currentMode
     */
    public void loadNormal() {
        loadData(null);
        model().saveRefreshTime();
    }

    /**
     * 页面上拉加载更多。上层最好不要调用，或者需要自己处理maxId，和currentMode
     */
    public void loadMore(String nextId) {
        loadData(nextId);
    }

    /**
     * 缓存数据刷新的数据
     */
    public void saveCache(List<D> cache) {
        model().saveCache(cache);
    }

    /**
     * 获取上次刷新的数据缓存
     */
    public List<D> getCache() {
        return model().getCache();
    }

    /**
     * 刷新的时间间隔是否达到应该自动刷新
     */
    private boolean shouldAutoRefresh() {
        return getAutoRefreshInterval() > 0 && (System.currentTimeMillis() - getAutoRefreshInterval() > model().getLastRefreshTime());
    }

    /**
     * 延迟700毫秒，执行下拉刷新。
     */
    private void refreshDelay() {
        if (refreshDelayOb != null && (!refreshDelayOb.isUnsubscribed())) {
            refreshDelayOb.unsubscribe();
        }
        refreshDelayOb = Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(PresenterEvent.UNBIND_VIEW))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        view().pullDownToRefresh(true);
                        refreshDelayOb = null;
                    }
                });
    }

    /**
     * 加载数据。上层最好不要调用，或者需要自己处理maxId，和currentMode
     *
     * @param nextId 为null时代表下拉刷新，不为null时为上拉加载更多
     */
    protected abstract void loadData(String nextId);
}
