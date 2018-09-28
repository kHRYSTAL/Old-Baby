package com.oldbaby.tabhome.view.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldbaby.R;
import com.oldbaby.common.view.TabButton;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.tab.FragTabPageMvps;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.tab.TabBarOnCreateListener;
import com.oldbaby.oblib.view.tab.TabBarView;
import com.oldbaby.oblib.view.tab.TabInfo;
import com.oldbaby.tabhome.model.HomeModelFactory;
import com.oldbaby.tabhome.presenter.TabHomePresenter;
import com.oldbaby.tabhome.view.ITabHomeView;
import com.oldbaby.video.view.impl.FragVideoList;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static final int TAB_ID_FEED = 1;
    public static final int TAB_ID_VIDEO = 2;
    public static final int TAB_ID_PROFILE = 3;

    // TODO: 18/9/27 需要替换类型
    Fragment fragFeedList; // feed流
    FragVideoList fragVideo; // 视频流
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
        return view;
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
                fragFeedList = new Fragment();
                return fragFeedList;
            }
            case TAB_ID_VIDEO: {
                fragVideo = new FragVideoList();
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
        TabInfo tabInfo = new TabInfo("全部", TAB_ID_FEED);
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


    @Override
    public String getPageName() {
        return PAGE_NAME;
    }
}
