package com.oldbaby.oblib.component.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldbaby.oblib.component.act.BaseFragmentActivity;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.component.lifeprovider.FragmentLifeProvider;
import com.oldbaby.oblib.mvp.view.IMvpView;
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
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.util.List;

/**
 * usage: Fragment 根基类
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragBase extends Fragment implements IMvpView, IConfirmDlgListener, ProgressDlgListener,
        PromptDlgListener, TipsDlgListener, MultiBtnDlgListener {

    protected FragmentLifeProvider lifeProvider = new FragmentLifeProvider();
    protected boolean isViewValid = false;// between view created and destroyed
    private IConfirmDlgMgr confirmDlgMgr;
    private IProgressDlgMgr progressDlgMgr;
    private IPromptDlgMgr promptDlgMgr;
    private ITipsDlgMgr tipsDlgMgr;
    private IMultiBtnDlgMgr multiBtnDlgMgr;
    private boolean isSelectedAsTab = false;

    @Override
    @CallSuper
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        //创建通用的确认对话框管理器
        confirmDlgMgr = ((OGApplication) getActivity().getApplication()).createConfirmDlgMgr();
        if (confirmDlgMgr != null) {
            confirmDlgMgr.setListener(this);
        }
        progressDlgMgr = ((OGApplication) getActivity().getApplication()).createProgressDlgMgr();
        if (progressDlgMgr != null) {
            progressDlgMgr.setListener(this);
        }
        promptDlgMgr = ((OGApplication) getActivity().getApplication()).createPromptDlgMgr();
        tipsDlgMgr = ((OGApplication) getActivity().getApplication()).createTipsDlgMgr();
        multiBtnDlgMgr = ((OGApplication) getActivity().getApplication()).createMultiBtnDlgMgr();
        lifeProvider.onNext(FragmentEvent.ATTACH);
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeProvider.onNext(FragmentEvent.CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.isViewValid = true;
        lifeProvider.onNext(FragmentEvent.CREATE);
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        //注入
        lifeProvider.onNext(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifeProvider.onNext(FragmentEvent.RESUME);
        if (!isHidden() && isSelectedAsTab()) {
            pageStart();
        }
    }

    @Override
    @CallSuper
    public void onPause() {
        if (!isHidden() && isSelectedAsTab()) {
            pageEnd();
        }
        lifeProvider.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        lifeProvider.onNext(FragmentEvent.STOP);
        super.onStop();
    }


    @Override
    @CallSuper
    public void onDestroyView() {
        this.isViewValid = false;
        lifeProvider.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        lifeProvider.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onDetach() {
        lifeProvider.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isResumed() && isSelectedAsTab()) {
            if (hidden) {
                pageEnd();
            } else {
                pageStart();
            }
        }
    }

    public void onSelectedChangedAsTab(boolean selected) {
        isSelectedAsTab = selected;
        if (isResumed() && !isHidden()) {
            if (selected) {
                pageStart();
            } else {
                pageEnd();
            }
        }
    }

    /**
     * 当前页面是否作为Tab的page。
     */
    public boolean isTabPage() {
        return false;
    }

    /**
     * 当前页面作为Tab的page 是否处于选中显示状态。
     * 如果该页面不是作为Tab的page，则返回true。
     */
    private boolean isSelectedAsTab() {
        if (isTabPage()) {
            return isSelectedAsTab;
        }
        return true;
    }

    /**
     * 当前fragment从不可见变为可见
     */
    @CallSuper
    protected void pageStart() {
        if (!reportSelf()) {
            Object obj = getActivity().getApplication();
            if (obj instanceof OGApplication) {
                ((OGApplication) obj).pageStart(this);
            }
        }
    }

    /**
     * 当前fragment从可见变为不可见
     */
    @CallSuper
    protected void pageEnd() {
        // 统计页面弹出
        if (!reportSelf()) {
            Object obj = getActivity().getApplication();
            if (obj instanceof OGApplication) {
                ((OGApplication) obj).pageEnd(this);
            }
        }
    }

    /**
     * 默认由父类FragBase统一做统计，默认值为false
     * 如果子类想自己做记录，则改为true
     */
    protected boolean reportSelf() {
        return false;
    }

    public abstract String getPageName();

    public boolean onBackPressed() {
        return false;
    }

    //==========rx java============
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return lifeProvider.bindUntilEvent(event);
    }

    //========== dialog start ============

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
            progressDlgMgr.show(getActivity(), content, cancel);
        }
    }

    @Override
    public void showProgressDlg(String tag, String content) {
        if (progressDlgMgr != null) {
            progressDlgMgr.show(getActivity(), tag, content, true);
        }
    }

    @Override
    public void showProgressDlg(String tag, String content, boolean cancel) {
        if (progressDlgMgr != null) {
            progressDlgMgr.show(getActivity(), tag, content, cancel);
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
    public void gotoUri(String uri) {
        ((OGApplication) getActivity().getApplication()).getUriMgr().router(getActivity(), uri);
    }

    @Override
    public void gotoUri(String uri, UriParam param) {
        ((OGApplication) getActivity().getApplication()).getUriMgr().router(getActivity(), uri, param);
    }

    @Override
    public void gotoUri(String uri, List<UriParam> params) {
        ((OGApplication) getActivity().getApplication()).getUriMgr().router(getActivity(), uri, params);
    }

    @Override
    public void showConfirmDlg(String tag, String title, String okText, String noText, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(getActivity(), tag, title, okText, noText, arg);
        }
    }

    @Override
    public void showConfirmDlg(String tag, String title, String content, String okText, String noText, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(getActivity(), tag, title, content, okText, noText, arg);
        }
    }

    @Override
    public void showConfirmDlg(String tag, String title, String content, String okText, String noText, boolean cancelable, Object arg) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.show(getActivity(), tag, title, content, okText, noText, cancelable, arg);
        }
    }

    @Override
    public void hideConfirmDlg(String tag) {
        if (confirmDlgMgr != null) {
            confirmDlgMgr.hide(tag);
        }
    }

    @Override
    public void showPromptDlg(String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener) {
        if (promptDlgMgr != null) {
            promptDlgMgr.show(getActivity(), tag, promptDlgAttr, listener);
        }
    }

    @Override
    public void showPromptDlg(String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener, PromptDlgTwoBtnListener twoBtnListener) {
        if (promptDlgMgr != null) {
            promptDlgMgr.show(getActivity(), tag, promptDlgAttr, listener, twoBtnListener);
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
            tipsDlgMgr.show(getActivity(), tag, tipsDlgAttr, listener);
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
            multiBtnDlgMgr.show(getActivity(), tag, multiBtnDlgAttr, listener);
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
        IntentUtil.intentToSystemSetting(getActivity());
    }

    @Override
    public void finishSelf() {
        this.getActivity().finish();
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
    public void onTipsCloseClick(Context context, String tag, Object args) {
        hideTipsDlg(tag);
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
    public void onMultiBtnOneClicked(Context context, String tag, Object arg) {
        hideMultiBtnDlg(tag);
    }

    @Override
    public void onMultiBtnTwoClicked(Context context, String tag, Object arg) {
        hideMultiBtnDlg(tag);
    }

    //========== dialog end ============

    public void updateTitleBarColor(int resId) {
        if (getActivity() instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) getActivity()).updateTitleBarColor(resId);
        }
    }

    public void updateStatusBarColor(int resId) {
        if (getActivity() instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) getActivity()).updateStatusBarColor(resId);
        }
    }

    public void updateTitleBarAndStatusBarColor(int resId) {
        if (getActivity() instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) getActivity()).updateTitleBarAndStatusBar(resId);
        }
    }
}
