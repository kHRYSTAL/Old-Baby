package com.oldbaby.oblib.mvp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldbaby.oblib.component.application.EBAppBackAndFore;
import com.oldbaby.oblib.component.frag.FragBase;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.rxjava.RxBus;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * usage: mvp开发基类 hold一组presenter实例 并负责presenter的保存和销毁, 以及presenter对view引用的管理
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragBaseMvps extends FragBase {

    private Map<String, BasePresenter> presenters;

    /**
     * 创建presenter 以及通过Android上下文的参数对presenter进行初始化
     * @return
     */
    protected abstract Map<String, BasePresenter> createPresenters();

    // app 前台可见
    public void onAppForeground() {}

    // app 切换至后台
    public void onAppBackground() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        RxBus.getDefault().toObservable(EBAppBackAndFore.class)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<EBAppBackAndFore>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Action1<EBAppBackAndFore>() {
                    @Override
                    public void call(EBAppBackAndFore ebAppBackAndFore) {
                        if (ebAppBackAndFore.getType()
                                == EBAppBackAndFore.TYPE_CUT_FOREGROUND) {
                            onAppForeground();
                        } else if (ebAppBackAndFore.getType()
                                == EBAppBackAndFore.TYPE_CUT_BACKGROUND) {
                            onAppBackground();
                        }
                    }
                });
    }

    @Nullable
    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenters = this.createPresenters();
        if (presenters != null) {
            for (BasePresenter p : presenters.values()) {
                p.setSchedulerSubscribe(Schedulers.io());
                p.setSchedulerObserver(AndroidSchedulers.mainThread());
                p.setSchedulerMain(AndroidSchedulers.mainThread());
                p.setSchedulerComputation(Schedulers.computation());
            }
        }
        if (presenters != null && savedInstanceState != null) {
            for (BasePresenter p : presenters.values()) {
                p.restoreState(savedInstanceState);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (BasePresenter p : presenters.values()) {
            p.saveState(outState);
        }
    }

    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (BasePresenter presenter : presenters.values()) {
            //注入
            presenter.bindView((IMvpView) this);
        }
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        for (BasePresenter presenter : presenters.values()) {
            presenter.onVisible();
        }
    }

    @Override
    @CallSuper
    public void onPause() {
        for (BasePresenter presenter : presenters.values()) {
            presenter.onInvisible();
        }
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        for (BasePresenter presenter : presenters.values()) {
            presenter.unbindView();
        }
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onDetach() {
        presenters.clear();
        super.onDetach();
    }

}
