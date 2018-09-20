package com.oldbaby.oblib.mvp.view.pullrefresh;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.oldbaby.oblib.R;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.presenter.pullrefresh.BasePullPresenter;
import com.oldbaby.oblib.mvp.view.FragBaseMvps;
import com.oldbaby.oblib.view.pulltorefresh.PullEvent;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * usage: 下拉刷新列表页面基类
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragBasePullMvps<V extends View, D extends LogicIdentifiable, P extends BasePullPresenter>
        extends FragBaseMvps implements IPullView<D>, PullToRefreshBase.OnRefreshListener2<V>  {

    private static final String KEY_PULL_PRESENTER = "PULL_PRESENTER";  //PullPresenter 的key

    protected PullToRefreshBase<V> pullView;
    protected V internalView;
    protected PullEvent currentEvent = PullEvent.none;  //当前下拉刷新的状态
    protected P basePullPresenter;
    protected String next;                                //对应next页面
    protected boolean isLastPage = true;                 //是否为最后一页，如果是，不可上拉加载更多

    private Subscription showLoadMoreDelayOb;      //load more 完成后，延迟300毫秒 scroll，使得新加载出来的数据得以显示。
    private PullAbility pullAbility = PullAbility.PULL_ABILITY_BOTH;  //页面是否有下拉和上拉的能力。默认为既可以下拉又可以上拉

    //region 生命周期
    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = createDefaultFragView();
        view.setLayoutParams(layout);

        pullView = (PullToRefreshBase<V>) view.findViewById(R.id.pullRefreshView);
        pullView.setOnRefreshListener((PullToRefreshBase.OnRefreshListener2) this);
        setPullModeWithPullAbility();
        internalView = pullView.getRefreshableView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        basePullPresenter.onViewResume();
    }
    //endregion

    //region Presenters相关
    @CallSuper
    @Override
    protected Map<String, BasePresenter> createPresenters() {
        Map<String, BasePresenter> presenterMap = new HashMap<>();
        basePullPresenter = makePullPresenter();
        presenterMap.put(KEY_PULL_PRESENTER, basePullPresenter);
        return presenterMap;
    }

    /**
     * 获取pullPresenter
     */
    public P getPullPresenter() {
        return basePullPresenter;
    }
    //endregion

    //region OnRefreshListener2 回调
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<V> refreshView) {
        if (this.isRefreshing() == false) {
            this.currentEvent = PullEvent.normal;
            basePullPresenter.loadNormal();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<V> refreshView) {
        if (this.isRefreshing() == false) {
            this.currentEvent = PullEvent.more;
            basePullPresenter.loadMore(next);
        }
    }
    //endregion

    //region currentEvent 相关

    /**
     * 判断是否在进行刷新
     */
    protected boolean isRefreshing() {
        return (this.currentEvent != PullEvent.none);
    }

    /**
     * 获取当前的刷新状态
     */
    protected PullEvent getCurrentEvent() {
        return this.currentEvent;
    }
    //endregion

    //region IPullView 方法实现

    @Override
    public final void pullDownToRefresh(boolean showRefreshingHeader) {
        if (!this.isRefreshing()) {
            pullView.resetCurrentMode();
            if (pullView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START || pullView.getCurrentMode() == PullToRefreshBase.Mode.BOTH) {
                pullView.setRefreshing(showRefreshingHeader);
            } else {
                //如果pullView 没有下拉能力。调用setRefreshing方法无效。
                this.currentEvent = PullEvent.normal;
                basePullPresenter.loadNormal();
            }
        }
    }

    @Override
    public final void pullUpToLoadMore(boolean showRefreshing) {
        if (!this.isRefreshing()) {
            currentEvent = PullEvent.more;
            basePullPresenter.loadMore(next);
            pullView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            pullView.setRefreshing(showRefreshing);
        }
    }

    @Override
    public final void onRefreshFinished() {
        this.pullView.onRefreshComplete();
        this.currentEvent = PullEvent.none;
    }

    @Override
    public final void onLoadSucessfully(List<D> items) {
        switch (currentEvent) {
            case normal:
                cleanData();
                getPullPresenter().saveCache(items);
                break;
            case more:
                if (items != null && items.size() > 0) {
                    scrollToShowMoreDelay();
                }
                break;
        }
        appendItems(items);
        showEmptyView();
        this.onRefreshFinished();
    }

    @Override
    public final void onLoadSucessfully(PageData<D> dataList) {
        if (dataList == null) {
            isLastPage = false;
        } else {
            this.next = dataList.next;
            if (dataList.data == null || dataList.data.isEmpty())
                isLastPage = true;
        }
        setPullModeWithPullAbility();
        onLoadSucessfully(dataList == null ? null : dataList.data);
    }

    @Override
    public final void onLoadFailed(Throwable failture) {
        if (next == null) {
            isLastPage = true;
        }
        setPullModeWithPullAbility();
        showErrorView();
        this.onRefreshFinished();
    }
    //endregion

    private void scrollToShowMoreDelay() {
        if (showLoadMoreDelayOb != null && (!showLoadMoreDelayOb.isUnsubscribed())) {
            showLoadMoreDelayOb.unsubscribe();
        }
        showLoadMoreDelayOb = Observable.timer(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        scrollToShowLoadMoreData();
                        showLoadMoreDelayOb = null;
                    }
                });
    }

    /**
     * load more 完成后，延迟300毫秒 scroll，使得新加载出来的数据得以显示。
     */
    protected abstract void scrollToShowLoadMoreData();

    /**
     * 创建pullPresenter
     */
    protected abstract P makePullPresenter();

    /**
     * 每个landing类可以通过此方法配置landing的页面布局
     *
     * @return
     */
    protected abstract View createDefaultFragView();

    /**
     * 设置页面是否有下拉和上拉的能力
     */
    protected void setPullAbility(PullAbility pullAbility) {
        this.pullAbility = pullAbility;
        setPullModeWithPullAbility();
    }

    /**
     * 根据页面的下拉和上拉的能力、当前是否为最后一页，来设置pullView的mode
     */
    private void setPullModeWithPullAbility() {
        if (pullView == null) {
            return;
        }
        PullToRefreshBase.Mode mode;
        if (isLastPage) {
            mode = PullToRefreshBase.Mode.PULL_FROM_START;
        } else {
            mode = PullToRefreshBase.Mode.BOTH;
        }
        switch (pullAbility) {
            case PULL_ABILITY_BOTH:
                pullView.setMode(mode);
                break;
            case PULL_ABILITY_TO_DOWN:
                if (mode == PullToRefreshBase.Mode.PULL_FROM_START || mode == PullToRefreshBase.Mode.BOTH) {
                    pullView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    pullView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                break;
            case PULL_ABILITY_TO_UP:
                if (mode == PullToRefreshBase.Mode.PULL_FROM_END || mode == PullToRefreshBase.Mode.BOTH) {
                    pullView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                } else {
                    pullView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                break;
            case PULL_ABILITY_NULL:
                pullView.setMode(PullToRefreshBase.Mode.DISABLED);
                break;
        }
    }

    public static enum PullAbility {
        PULL_ABILITY_BOTH, PULL_ABILITY_TO_DOWN, PULL_ABILITY_TO_UP, PULL_ABILITY_NULL
    }

}
