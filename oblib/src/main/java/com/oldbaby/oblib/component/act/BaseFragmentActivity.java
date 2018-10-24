package com.oldbaby.oblib.component.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.oldbaby.oblib.component.application.EBAppBackAndFore;
import com.oldbaby.oblib.component.application.EBExit;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.component.lifeprovider.ActivityLifeProvider;
import com.oldbaby.oblib.mvp.view.IMvpView;
import com.oldbaby.oblib.rxjava.RxBus;
import com.oldbaby.oblib.uri.UriParam;
import com.oldbaby.oblib.util.IntentUtil;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.oblib.view.dialog.IConfirmDlgListener;
import com.oldbaby.oblib.view.dialog.IConfirmDlgMgr;
import com.oldbaby.oblib.view.dialog.IMultiBtnDlgMgr;
import com.oldbaby.oblib.view.dialog.IProgressDlgMgr;
import com.oldbaby.oblib.view.dialog.IPromptDlgMgr;
import com.oldbaby.oblib.view.dialog.ITipsDlgMgr;
import com.oldbaby.oblib.view.dialog.MultiBtnDlgAttr;
import com.oldbaby.oblib.view.dialog.MultiBtnDlgListener;
import com.oldbaby.oblib.view.dialog.ProgressDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgAttr;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgTwoBtnListener;
import com.oldbaby.oblib.view.dialog.TipsDlgAttr;
import com.oldbaby.oblib.view.dialog.TipsDlgListener;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import skin.support.app.SkinCompatActivity;

/**
 * usage: Activity根基类
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class BaseFragmentActivity extends AppCompatActivity implements IMvpView, IConfirmDlgListener,
        ProgressDlgListener, PromptDlgListener, TipsDlgListener, MultiBtnDlgListener {

    protected String logTag;
    protected Handler handler = new Handler();
    protected LayoutInflater inflater;
    private boolean isStopped = false;

    // 是否开启onSaveInstanceState 功能，默认为关闭
    private boolean saveInstanceEnable = false;
    protected ActivityLifeProvider lifeProvider = new ActivityLifeProvider();
    private IConfirmDlgMgr confirmDlgMgr;
    private IProgressDlgMgr progressDlgMgr;
    private IPromptDlgMgr promptDlgMgr;
    private ITipsDlgMgr tipsDlgMgr;
    private IMultiBtnDlgMgr multiBtnDlgMgr;

    //region 生命周期的方法

    /**
     * 如果不符合启动条件，返回false，activity会自动关闭，否则返回true
     *
     * @param savedInstanceState
     * @return
     */
    public boolean shouldContinueCreate(Bundle savedInstanceState) {
        return true;
    }

    /**
     * call super and configure window title feature
     */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        this.logTag = this.getClass().getSimpleName();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (shouldContinueCreate(savedInstanceState)) {
            onContinueCreate(savedInstanceState);
        } else {
            finish();
        }
    }

    /**
     * 如果 shouldContinueCreate(Bundle)
     * 返回true，则执行本函数，否则将跳过此函数
     *
     * @param savedInstanceState
     */
    @CallSuper
    public void onContinueCreate(Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getApplicationContext());
        lifeProvider.onNext(ActivityEvent.CREATE);
        confirmDlgMgr = ((OGApplication) getApplication()).createConfirmDlgMgr();
        if (confirmDlgMgr != null)
            confirmDlgMgr.setListener(this);
        progressDlgMgr = ((OGApplication) getApplication()).createProgressDlgMgr();
        if (progressDlgMgr != null) {
            progressDlgMgr.setListener(this);
            promptDlgMgr = ((OGApplication) getApplication()).createPromptDlgMgr();
        }
        tipsDlgMgr = ((OGApplication) getApplication()).createTipsDlgMgr();
        multiBtnDlgMgr = ((OGApplication) getApplication()).createMultiBtnDlgMgr();
        RxBus.getDefault().toObservable(EBAppBackAndFore.class)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<EBAppBackAndFore>bindUntilEvent(ActivityEvent.DESTROY))
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
        RxBus.getDefault().toObservable(EBExit.class)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<EBExit>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Action1<EBExit>() {
                    @Override
                    public void call(EBExit ebExit) {
                        if (ebExit.getType() == EBExit.TYPE_EXIT) {
                            finishSelf();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifeProvider.onNext(ActivityEvent.START);
        isStopped = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 18/9/20 友盟埋点
        lifeProvider.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        lifeProvider.onNext(ActivityEvent.PAUSE);
        // TODO: 18/9/20 友盟埋点
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifeProvider.onNext(ActivityEvent.STOP);
        isStopped = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifeProvider.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    /**
     * 是否开启onSaveInstanceState 功能，默认为关闭
     *
     * @param state true ：开启， false ：关闭
     */
    public void setSaveInstanceEnable(boolean state) {
        this.saveInstanceEnable = state;
    }

    /**
     * Activity 不保存任何状态，当Activity被回收后，重启时全部重新加载。
     * 目的当Activity被销毁后，如果包含多个Fragment时，会重复Add。如果子类有保存状态的需求可以将 saveInstanceEnable 设为true
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (saveInstanceEnable) {
            super.onSaveInstanceState(outState);
        }
    }

    public boolean isStopped() {
        return isStopped;
    }

    //endregion

    //region application的事件

    // app前台可见
    public void onAppForeGround() {

    }

    // app切到后台
    public void onAppBackGround() {

    }

    //endregion


    //region swipeback methods

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        return v;
    }
    //endregion

    //region RxJava methods

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return lifeProvider.bindUntilEvent(event);
    }

    //endregion

    //region ImvpView implementation

    @Override
    public void showProgressDlg() {
        showProgressDlg(null);
    }

    @Override
    public void showProgressDlg(String content) {
        showProgressDlg(content, true);
    }

    @Override
    public void showProgressDlg(String content, boolean cancel) {
        if (progressDlgMgr != null) {
            progressDlgMgr.show(this, content, cancel);
        }
    }

    @Override
    public void showProgressDlg(String tag, String content) {
        if (progressDlgMgr != null) {
            progressDlgMgr.show(this, tag, content, true);
        }
    }

    @Override
    public void showProgressDlg(String tag, String content, boolean cancel) {
        if (progressDlgMgr != null) {
            progressDlgMgr.show(this, tag, content, cancel);
        }
    }

    @Override
    public void hideProgressDlg() {
        if (progressDlgMgr != null) {
            progressDlgMgr.hide();
        }
    }

    @Override
    public void hideProgressDlg(String tag) {
        if (progressDlgMgr != null) {
            progressDlgMgr.hide(tag);
        }
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.showShort(toast);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void gotoUri(String uri) {
        ((OGApplication) getApplication()).getUriMgr().router(this, uri);
    }

    @Override
    public void gotoUri(String uri, UriParam param) {
        ((OGApplication) getApplication()).getUriMgr().router(this, uri, param);
    }

    @Override
    public void gotoUri(String uri, List<UriParam> params) {
        ((OGApplication) getApplication()).getUriMgr().router(this, uri, params);
    }

    @Override
    public void showConfirmDlg(String tag, String title, String okText, String noText, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(this, tag, title, okText, noText, arg);
        }
    }

    @Override
    public void showConfirmDlg(String tag, String title, String content, String okText, String noText, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(this, tag, title, content, okText, noText, arg);
        }
    }

    @Override
    public void showConfirmDlg(String tag, String title, String content, String okText, String noText, boolean cancelable, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(this, tag, title, content, okText, noText, cancelable, arg);
        }
    }

    @Override
    public void hideConfirmDlg(String tag) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.hide(tag);
        }
    }

    @Override
    public void finishSelf() {
        finish();
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    public void showPromptDlg(String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener) {
        if (promptDlgMgr != null) {
            promptDlgMgr.show(this, tag, promptDlgAttr, listener);
        }
    }

    @Override
    public void showPromptDlg(String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener, PromptDlgTwoBtnListener twoBtnListener) {
        if (promptDlgMgr != null) {
            promptDlgMgr.show(this, tag, promptDlgAttr, listener, twoBtnListener);
        }
    }

    @Override
    public void hidePromptDlg(String tag) {
        if (promptDlgMgr != null) {
            promptDlgMgr.hide(tag);
        }
    }

    @Override
    public boolean isPromptDlgShowing(String tag) {
        if (promptDlgMgr != null) {
            return promptDlgMgr.isShowing(tag);
        }
        return false;
    }

    @Override
    public void showTipsDlg(String tag, TipsDlgAttr tipsDlgAttr, TipsDlgListener listener) {
        if (tipsDlgMgr != null) {
            tipsDlgMgr.show(this, tag, tipsDlgAttr, listener);
        }
    }

    @Override
    public void hideTipsDlg(String tag) {
        if (tipsDlgMgr != null) {
            tipsDlgMgr.hide(tag);
        }
    }

    @Override
    public boolean isTipsDlgShowing(String tag) {
        if (tipsDlgMgr != null) {
            return tipsDlgMgr.isShowing(tag);
        }
        return false;
    }

    @Override
    public void showMultiBtnDlg(String tag, MultiBtnDlgAttr multiBtnDlgAttr, MultiBtnDlgListener listener) {
        if (multiBtnDlgMgr != null) {
            multiBtnDlgMgr.show(this, tag, multiBtnDlgAttr, listener);
        }
    }

    @Override
    public void hideMultiBtnDlg(String tag) {
        if (multiBtnDlgMgr != null) {
            multiBtnDlgMgr.hide(tag);
        }
    }

    @Override
    public boolean isMultiBtnDlgShowing(String tag) {
        if (multiBtnDlgMgr != null) {
            return multiBtnDlgMgr.isShowing(tag);
        }
        return false;
    }

    @Override
    public void showSystemPermissionDlg(String permissionPrompt) {
        showConfirmDlg(TAG_CONFIRM_DLG_SYSTEM_PERMISSION, permissionPrompt, "去设置开启", "取消", null);
    }

    @Override
    public void goToSysSetting() {
        IntentUtil.intentToSystemSetting(this);
    }

    @Override
    public void onOkClicked(Context context, String tag, Object arg) {
        if (StringUtil.isEquals(tag, TAG_CONFIRM_DLG_SYSTEM_PERMISSION)) {
            goToSysSetting();
        }
        hideConfirmDlg(tag);
    }

    @Override
    public void onNoClicked(Context context, String tag, Object arg) {
        hideConfirmDlg(tag);
    }

    @Override
    public void onPromptClicked(Context context, String tag, Object arg) {
        hidePromptDlg(tag);
    }

    @Override
    public void onCancel(Context context, String tag) {
    }

    @Override
    public void onDismiss(Context context, String tag) {
    }

    @Override
    public void onTipsCloseClick(Context context, String tag, Object args) {
        hideTipsDlg(tag);
    }

    @Override
    public void onMultiBtnOneClicked(Context context, String tag, Object arg) {
        hideMultiBtnDlg(tag);
    }

    @Override
    public void onMultiBtnTwoClicked(Context context, String tag, Object arg) {
        hideMultiBtnDlg(tag);
    }

    //endregion


    //region 抽象方法

    /**
     * TITLE_NONE, TITLE_CUS, TITLE_LAYOUT
     */
    protected abstract int titleType();

    protected abstract void configStartAnim(Intent intent);
    //endregion

    // 修改状态栏颜色
    public abstract void updateStatusBarColor(int resId);

    // 修改标题栏颜色
    public abstract void updateTitleBarColor(int resId);

    // 修改状态栏和标题栏颜色
    public abstract void updateTitleBarAndStatusBar(int resId);

    public abstract void hideStatusBar();
}
