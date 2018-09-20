package com.oldbaby.oblib.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.oldbaby.oblib.component.lifeprovider.IPresenterLifecycleProvider;
import com.oldbaby.oblib.component.lifeprovider.PresenterEvent;
import com.oldbaby.oblib.component.lifeprovider.PresenterLifeProvider;
import com.oldbaby.oblib.mvp.view.IMvpView;
import com.oldbaby.oblib.mvp.model.IMvpModel;
import com.oldbaby.oblib.rxjava.RxBus;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.lang.ref.WeakReference;

import rx.Scheduler;

/**
 * usage: 所有presenter基类
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class BasePresenter<M extends IMvpModel, V extends IMvpView> {

    private M model; // 对应的model
    private Scheduler schedulerSubscribe;//被观察者运行线程
    private Scheduler schedulerObserver;//观察者运行线程
    private Scheduler schedulerComputation;//计算运行线程
    private Scheduler schedulerMain;//主线程
    private IPresenterLifecycleProvider lifeProvider;//主导器生命周期事件提供者
    private WeakReference<V> view;
    private RxBus rxBus = RxBus.getDefault();

    public BasePresenter() {
        lifeProvider = new PresenterLifeProvider();
    }

    /**
     * 注入Model
     *
     * @param model
     */
    public void setModel(M model) {
        this.model = model;
        if (setupDone()) {
            updateView();
        }
    }

    /**
     * 绑定view
     *
     * @param view
     */
    public void bindView(@NonNull V view) {
        this.view = new WeakReference<>(view);
        lifeProvider.onNext(PresenterEvent.BIND_VIEW);
        if (setupDone()) {
            updateView();
        }
    }

    /**
     * 视图可见
     */
    public void onVisible() {

    }

    /**
     * 视图变为不可见
     */
    public void onInvisible() {

    }

    /**
     * 解绑view
     */
    public void unbindView() {
        lifeProvider.onNext(PresenterEvent.UNBIND_VIEW);
        this.view = null;
    }

    /**
     * 获取当前的view
     *
     * @return 可能为空
     */
    protected V view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }

    /**
     * 获取当前的model
     *
     * @return
     */
    protected M model() {
        return model;
    }

    public RxBus rxBus() {
        return rxBus;
    }

    public void setRxBus(RxBus rxBus) {
        this.rxBus = rxBus;
    }

    /**
     * 当绑定view或者注入model时，如果view与model已同时准备好，并且需要更新view，重载此方法
     */
    protected void updateView() {

    }


    /**
     * 判断view与model是否已经同时准备好
     *
     * @return
     */
    protected boolean setupDone() {
        return view() != null && model != null;
    }

    /**
     * 恢复数据状态，主要用于屏幕旋转、设置变化等场景
     *
     * @param savedInstanceState
     * @link #saveState}
     */
    public void restoreState(Bundle savedInstanceState) {
    }

    /**
     * 存储数据状态，主要用于屏幕旋转、设置变化等场景
     * {@link #restoreState}
     *
     * @param outState
     */
    public void saveState(Bundle outState) {
    }


    /**
     * 获取发布者的线程scheduler
     *
     * @return
     */
    public Scheduler getSchedulerSubscribe() {
        return schedulerSubscribe;
    }

    /**
     * 设置发布者的线程scheduler
     *
     * @param schedulerSubscribe
     */
    public void setSchedulerSubscribe(Scheduler schedulerSubscribe) {
        this.schedulerSubscribe = schedulerSubscribe;
    }

    /**
     * 获取观察者的线程scheduler
     *
     * @return
     */
    public Scheduler getSchedulerObserver() {
        return schedulerObserver;
    }

    /**
     * 设置观察者的线程schduler
     *
     * @param schedulerObserver
     */
    public void setSchedulerObserver(Scheduler schedulerObserver) {
        this.schedulerObserver = schedulerObserver;
    }

    /**
     * 获取计算线程scheduler
     *
     * @return
     */
    public Scheduler getSchedulerComputation() {
        return schedulerComputation;
    }

    /**
     * 获取计算线程scheduler
     *
     * @return
     */
    public void setSchedulerComputation(Scheduler schedulerComputation) {
        this.schedulerComputation = schedulerComputation;
    }

    /**
     * 获取主线程scheduler
     *
     * @return
     */
    public Scheduler getSchedulerMain() {
        return schedulerMain;
    }

    /**
     * 设置主线程schduler
     *
     * @param schedulerObserver
     */
    public void setSchedulerMain(Scheduler schedulerObserver) {
        this.schedulerMain = schedulerObserver;
    }

    /**
     * 与对应的android组建生命周期进行绑定
     *
     * @param event
     * @param <T>
     * @return
     */
    protected <T> LifecycleTransformer<T> bindUntilEvent(@NonNull PresenterEvent event) {
        return lifeProvider.bindUntilEvent(event);
    }
    //endregion


    //region 一些给子类重载的方法
    public void onConfirmOkClicked(String tag, Object arg) {

    }

    public void onConfirmNoClicked(String tag, Object arg) {

    }

    public void onPromptClicked(String tag, Object arg) {

    }

    public void onTipsCloseClick(String tag, Object arg) {

    }

    public void onMultiBtnOneClick(String tag, Object arg) {

    }

    public void onMultiBtnTwoClick(String tag, Object arg) {

    }
}
