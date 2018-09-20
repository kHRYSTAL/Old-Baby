package com.oldbaby.oblib.component.lifeprovider;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class PresenterLifeProvider implements IPresenterLifecycleProvider {

    private final BehaviorSubject<PresenterEvent> lifecycleSubject = BehaviorSubject.create();

    /**
     * 发射信号
     */
    public void onNext(PresenterEvent event) {
        lifecycleSubject.onNext(event);
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<PresenterEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull PresenterEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

}
