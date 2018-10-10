package com.oldbaby.tabhome.view.impl;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldbaby.R;
import com.oldbaby.common.view.TabButton;
import com.oldbaby.feed.view.impl.FragFeedTab;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.tab.FragTabPageMvps;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.dialog.PromptDlgAttr;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgTwoBtnListener;
import com.oldbaby.oblib.view.tab.TabBarOnCreateListener;
import com.oldbaby.oblib.view.tab.TabBarView;
import com.oldbaby.oblib.view.tab.TabInfo;
import com.oldbaby.tabhome.model.HomeModelFactory;
import com.oldbaby.tabhome.presenter.TabHomePresenter;
import com.oldbaby.tabhome.view.ITabHomeView;
import com.oldbaby.video.view.impl.FragVideoTab;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.jzvd.Jzvd;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class FragTabHome extends FragTabPageMvps implements ITabHomeView {

    private static final String TAG = "TabHome";
    private static final String PAGE_NAME = "";
    private static final String TAG_DIALOG_PROMPT_SETTING = "tag_dialog_prompt_setting";
    private static final String TAG_DIALOG_PROMPT_RATIONALE = "tag_dialog_prompt_rationale";

    public static final int TAB_ID_FEED = 1;
    public static final int TAB_ID_VIDEO = 2;
    public static final int TAB_ID_PROFILE = 3;

    // TODO: 18/9/27 需要替换类型
    FragFeedTab fragFeed; // feed流
    FragVideoTab fragVideo; // 视频流
    Fragment fragProfile; // 个人中心

    ArrayList<TabInfo> tabs; // tab集合 按照从左到右顺序

    // TODO 拉取闪屏 presenter


    @Override
    protected Map<String, BasePresenter> createPresenters() {
        Map<String, BasePresenter> presenterMap = new HashMap<>();
        TabHomePresenter tabHomePresenter = new TabHomePresenter();
        tabHomePresenter.setModel(HomeModelFactory.CreateTabHomeModel());
        presenterMap.put(tabHomePresenter.getClass().getSimpleName(), tabHomePresenter);
        return presenterMap;
    }

    @Override
    protected View createDefaultFragView() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.frag_home, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        tabBar.setBottomIndicator(false);
        ButterKnife.bind(this, view);
        registerRxBus();
        // 构造所有用到的presenter
        setupPresenters();
        //TODO 开启消息轮询
        //TODO 发送一个关闭splash的通知
        // 请求通用系统权限
        configJZVD();
        startRequestCommonPermission();
        return view;
    }

    private void configJZVD() {
        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void startRequestCommonPermission() {
        // 存储与读取网络状态
        AndPermission.with(getActivity()).runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE)
                .rationale(mRationale)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {

                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                            // 这些权限被用户总是拒绝。
                            PromptDlgAttr promptDlgAttr = new PromptDlgAttr();
                            promptDlgAttr.title = "无法继续运行";
                            promptDlgAttr.subTitle = "请去设置页进行授权";
                            promptDlgAttr.cancelable = false;
                            promptDlgAttr.showClose = false;

                            promptDlgAttr.btnText = "立即授权";
                            promptDlgAttr.btnBgResId = R.drawable.sel_btn_sc_bg;
                            showPromptDlg(TAG_DIALOG_PROMPT_SETTING, promptDlgAttr, new PromptDlgListener() {
                                @Override
                                public void onPromptClicked(Context context, String tag, Object arg) {
                                    hidePromptDlg(TAG_DIALOG_PROMPT_SETTING);
                                    AndPermission.with(getActivity())
                                            .runtime()
                                            .setting()
                                            .onComeback(new Setting.Action() {
                                                @Override
                                                public void onAction() {
                                                    // 用户从设置回来了。
                                                }
                                            }).start();
                                }
                            });
                        }
                    }
                }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    private void setupPresenters() {

    }

    /**
     * 注册rxbus
     */
    private void registerRxBus() {

    }

    /**
     * 解绑rxbus
     */
    private void unRegisterRxBus() {

    }

    @Override
    protected Fragment createTabPage(TabInfo tabInfo) {
        MLog.d(TAG, "createTabPage : " + tabInfo.name);
        switch (tabInfo.tabId) {
            case TAB_ID_FEED: {
                fragFeed = new FragFeedTab();
                return fragFeed;
            }
            case TAB_ID_VIDEO: {
                fragVideo = new FragVideoTab();
                return fragVideo;
            }
            case TAB_ID_PROFILE: {
                fragProfile = new Fragment();
                return fragProfile;
            }

        }
        return null;
    }

    @Override
    protected ArrayList<TabInfo> tabToAdded() {
        tabs = new ArrayList<>();
        TabInfo tabInfo = new TabInfo("资讯", TAB_ID_FEED);
        tabInfo.arg1 = R.drawable.sel_tab_feed;
        tabs.add(tabInfo);

        tabInfo = new TabInfo("视频", TAB_ID_VIDEO);
        tabInfo.arg1 = R.drawable.sel_tab_feed;
        tabs.add(tabInfo);

        tabInfo = new TabInfo("设置", TAB_ID_PROFILE);
        tabInfo.arg1 = R.drawable.sel_tab_feed;
        tabs.add(tabInfo);
        return tabs;
    }

    @Override
    protected boolean needPreLoad(TabInfo tabInfo) {
        if (tabInfo != null) {
            //如果tab为 邻里、人脉、我的,则预加载
            if (tabInfo.tabId == TAB_ID_FEED || tabInfo.tabId == TAB_ID_VIDEO || tabInfo.tabId == TAB_ID_PROFILE) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean scrollable() {
        return false;
    }

    protected TabBarOnCreateListener getCreateTabListener() {
        return new TabBarOnCreateListener() {
            @Override
            public TabButton createTabView(TabBarView view, TabInfo tab, int atIndex) {
                TabButton tb = new TabButton(getActivity(), tab.tabId);
                tb.icon.setImageResource(tab.arg1);
                tb.text.setText(tab.name);
                return tb;
            }

            @Override
            public void selectTabView(View view, TabInfo tabInfo) {
                TabButton tb = (TabButton) view;
                tb.icon.setSelected(true);
                tb.text.setTextColor(getResources().getColor(R.color.tabbar_text_sel_color));
                tb.hideRedDot();
                // 切换tab时 所有视频都暂停
                Jzvd.releaseAllVideos();
            }

            @Override
            public void unSelectTabView(View view) {
                TabButton tb = (TabButton) view;
                tb.icon.setSelected(false);
                tb.text.setTextColor(getResources().getColor(
                        R.color.color_black_45));
            }
        };
    }

    private Rationale mRationale = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, final RequestExecutor executor) {
            // 这里使用一个Dialog询问用户是否继续授权。
            PromptDlgAttr promptDlgAttr = new PromptDlgAttr();
            promptDlgAttr.title = "确定取消授权?";
            promptDlgAttr.subTitle = "没有该功能程序将无法使用";
            promptDlgAttr.cancelable = false;
            promptDlgAttr.showClose = false;

            promptDlgAttr.isTwoBtn = true;
            promptDlgAttr.rightBtnText = "重新授权";
            promptDlgAttr.rightBtnBgResId = R.drawable.sel_btn_sc_bg;
            promptDlgAttr.rightBtnTextColorId = R.color.white;

            promptDlgAttr.leftBtnText = "取消授权";
            promptDlgAttr.leftBtnBgResId = R.drawable.sel_btn_sc_bg;
            promptDlgAttr.leftBtnTextColorId = R.color.white;

            showPromptDlg(TAG_DIALOG_PROMPT_RATIONALE, promptDlgAttr, null, new PromptDlgTwoBtnListener() {
                @Override
                public void onPromptLeftClicked(Context context, String tag, Object arg) {
                    hidePromptDlg(TAG_DIALOG_PROMPT_RATIONALE);
                    executor.cancel();
                }

                @Override
                public void onPromptRightClicked(Context context, String tag, Object arg) {
                    hidePromptDlg(TAG_DIALOG_PROMPT_RATIONALE);
                    executor.execute();
                }
            });
        }
    };

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }
}
