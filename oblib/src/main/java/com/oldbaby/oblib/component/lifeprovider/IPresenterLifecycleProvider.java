package com.oldbaby.oblib.component.lifeprovider;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;

import rx.Observable;

/**
 * usage: presenter生命周期事件提供者
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IPresenterLifecycleProvider {
    // 发射一个presenter生命周期事件
    void onNext(PresenterEvent event);

    /**
     * @return a sequence of {@link android.app.Activity} lifecycle events
     */
    @NonNull
    @CheckResult
    Observable<PresenterEvent> lifecycle();

    /**
     * 用于将一个RXJava的观察者回调绑定到主导器的一个事件上
     */
    @NonNull
    @CheckResult
    <T> LifecycleTransformer<T> bindUntilEvent(@NonNull PresenterEvent event);
}
