package com.oldbaby.oblib.mvp.view.tab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.component.frag.FragBase;
import com.oldbaby.oblib.mvp.view.FragBaseMvps;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.SwipeView;
import com.oldbaby.oblib.view.tab.TabBarOnCreateListener;
import com.oldbaby.oblib.view.tab.TabBarView;
import com.oldbaby.oblib.view.tab.TabBarViewListener;
import com.oldbaby.oblib.view.tab.TabInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * usage: 多tab的fragment。
 * 需要定制布局 重写createDefaultFragView。
 * 四个抽象方法需要实现 ：createTabPage tabToAdded needPreLoad scrollable
 * 需要定制tab的样式个功能重写 getCreateTabListener
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragTabPageMvps extends FragBaseMvps implements TabBarViewListener, SwipeView.OnPageChangedListener {
    private static final String TAG = "FragTabPageMvps";

    protected View rootView;  //页面根view
    protected TabBarView tabBar;
    protected FrameLayout flContainer;   //不可滚动的情况，fragment的容器
    protected SwipeView svContainer;     //可以滚动的情况，fragment的容器

    private Fragment[] pages = null;     //每个tab对应的fragment
    private HashMap<TabInfo, View> pageHolders;// 如果是可滚动情况，用来占位
    private ArrayList<TabInfo> tabs;

    private int curIndex = -1;
    private int tabCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = createDefaultFragView();
        ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layout);

        initView();
        initTabView();
        initPageViews();
        return rootView;
    }

    @CallSuper
    @Override
    protected void pageStart() {
        super.pageStart();
        //处理每个tab的page页面的统计。
        trackerPage(curIndex, -1);
    }

    @CallSuper
    @Override
    protected void pageEnd() {
        super.pageEnd();
        //处理每个tab的page页面的统计。
        trackerPage(-1, curIndex);
    }

    //region 将fragment的一些通知和回调分发到page fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (curIndex >= 0 && pages[curIndex].isAdded()) {
            pages[curIndex].onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menu) {
        boolean res = this.pages[curIndex].onContextItemSelected(menu);
        if (!res)
            res = super.onContextItemSelected(menu);
        return res;
    }
    //endregion

    //region TabBarViewListener

    @Override
    public boolean shouldSelectTab(TabBarView view, TabInfo tab, int atIndex) {
        //指定tab是否可以被选中，返回selectable的结果，selectable方法子类可以重写。
        //OnPageChangedListener中的shouldSelect，也调用selectable。统一点击tab和滑动屏幕切换tab时的处理。
        return selectable(curIndex, atIndex);
    }

    @Override
    public final void didSelectTabBar(TabBarView view, TabInfo tab, int atIndex) {
        //tab的选中状态改变，切换对应的page
        selectPage(atIndex, false);
    }
    //endregion

    //region SwipeView.OnPageChangedListener
    @Override
    public final boolean shouldSelect(int oldPage, int newPage) {
        return selectable(oldPage, newPage);
    }

    @Override
    public final void onPageChanged(int oldPage, int newPage) {
        selectPage(newPage, true);
    }
    //endregion

    //region 私有方法
    //初始view
    private void initView() {
        tabBar = (TabBarView) rootView.findViewById(R.id.frag_tab_page_tab);
        if (scrollable()) {
            svContainer = (SwipeView) rootView.findViewById(R.id.frag_tab_page_container);
            svContainer.setOnPageChangedListener(this);
        } else {
            flContainer = (FrameLayout) rootView.findViewById(R.id.frag_tab_page_container);
        }
    }

    //初始化tabView
    private final void initTabView() {
        tabBar.setListener(this);
        tabBar.setCreateListener(getCreateTabListener());
        tabs = tabToAdded();
        tabCount = tabs.size();
        this.tabBar.setTabs(tabs);
    }

    //初始化pageView
    private final void initPageViews() {
        pages = new Fragment[tabCount];
        pageHolders = new HashMap<TabInfo, View>(tabCount);

        for (int i = 0, size = tabs.size(); i < size; i++) {
            TabInfo info = tabs.get(i);
            if (scrollable()) {
                //如果是可滑动的情况，view用SwipeView，每个fragment对应一个singlePageContainer，切换tab时滚动view。
                int containerId = getResources().getIdentifier("tab" + i, "id", getActivity().getPackageName());
                LinearLayout singlePageContainer = new LinearLayout(getActivity());
                singlePageContainer.setBackgroundColor(Color.TRANSPARENT);
                singlePageContainer.setId(containerId);
                pageHolders.put(info, singlePageContainer);
                svContainer.addView(singlePageContainer);
            }
            if (needPreLoad(info)) {
                tryLoadPage(i, true);
            }
        }
    }

    /**
     * 切换到指定index的page
     *
     * @param shouldScrollTabBar 是否滚动tabbar。如果为true，是page滑动触发tab同步；如果是false，是tab点击触发，page同步
     */
    private void selectPage(final int index, final boolean shouldScrollTabBar) {

        //该方法之前用handle post 的方式执行。curIndex的改变和didSelectTab的回调 在shouldScrollTabBar为true时延迟250毫秒执行。
        //这样方式，导致统计发生异常。后将handle 和 延迟250毫秒去掉，如有异常可以考虑之前的方案。

        MLog.d(TAG, "select page " + index);

        if (shouldScrollTabBar) {
            tabBar.setSelectedIndex(index, true, false);
        } else {
            if (scrollable()) {
                //如果是可滑动的情况，view用SwipeView，每个fragment对应一个singlePageContainer，切换tab时滚动view。
                svContainer.scrollToPage(index);
            } else {
                //如果是非可滑动的情况，所有fragment都依附在flContainer上，通过show和hide，切换显示
                View v;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment showFragment = null;
                //根据统计的需求，需要先隐藏该隐藏的tab，再显示该显示的tab。
                for (int i = 0; i < tabs.size(); i++) {
                    if (pages[i] == null) {
                        continue;
                    }
                    if (i == index) {
                        showFragment = pages[i];
                    } else {
                        ft.hide(pages[i]);
                    }
                }
                if (showFragment != null) {
                    ft.show(showFragment);
                }
                ft.commit();
            }
        }

        int indexBefor = curIndex;
        curIndex = index;
        tryLoadPage(index, false);
        if (scrollable() && isResumed()) {
            //如果是可滑动的情况，子fragment 的统计 由FragTabPageMvps统一处理。
            trackerPage(curIndex, indexBefor);
        }
        //统一由tab触发和由page触发的切换
        didSelectTab(tabs.get(index), indexBefor < 0 ? null : tabs.get(indexBefor));
    }

    /**
     * 加载并添加 指定index的page
     */
    private void tryLoadPage(int index, boolean isPreLoad) {
        if (pages[index] != null)
            return;
        TabInfo tabInfo = tabs.get(index);
        pages[index] = createTabPage(tabInfo);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (scrollable()) {
            //如果是可滑动的情况，view用SwipeView，每个fragment对应一个singlePageContainer，切换tab时滚动view。
            View pageContainer = pageHolders.get(tabInfo);
            ft.add(pageContainer.getId(), pages[index]);
        } else {
            //如果是非可滑动的情况，所有fragment都依附在flContainer上，通过show和hide，切换显示
            ft.add(R.id.frag_tab_page_container, pages[index]);
            if (isPreLoad) {
                ft.hide(pages[index]);
            }
        }
        ft.commit();
    }

    /**
     * 如果是可滑动的情况，子fragment 的统计 由FragTabPageMvps统一处理。
     * index对应的fragment视为页面从不见到可见
     * indexBefore对应的fragment视为页面从可见到不可见
     */
    private void trackerPage(int startPage, int endPage) {
        //不可滑动的情况，子fragment是通过hide 和 show来做的切换，不需要特殊处理。
        if (!scrollable())
            return;

        //根据统计需要，先执行隐藏，再执行显示
        if (endPage >= 0 && pages[endPage] != null && pages[endPage] instanceof FragBase) {
            FragBase fragBeforTmp = ((FragBase) pages[endPage]);
            if (!fragBeforTmp.isTabPage()) {
                throw new RuntimeException("FragTabPageMvps 在可以左右滑动时，子fragment 需要重写isTabPage 返回true");
            }
            fragBeforTmp.onSelectedChangedAsTab(false);
        }

        if (startPage >= 0 && pages[startPage] != null && pages[startPage] instanceof FragBase) {
            FragBase fragCurTmp = ((FragBase) pages[startPage]);
            if (!fragCurTmp.isTabPage()) {
                throw new RuntimeException("FragTabPageMvps 在可以左右滑动时，子fragment 需要重写isTabPage 返回true");
            }
            fragCurTmp.onSelectedChangedAsTab(true);
        }
    }
    //endregion

    //region 提供给子类调用的方法

    /**
     * 获取当前tab的page fragment
     */
    protected Fragment getCurPage() {
        return curIndex >= 0 ? pages[curIndex] : null;
    }

    /**
     * 获取当前tab的page fragment
     */
    protected final Fragment getPage(int index) {
        if (index > 0 && pages != null && index < pages.length) {
            return pages[index];
        } else {
            return null;
        }
    }

    /**
     * 获取当前tab的索引
     */
    protected int getCurIndex() {
        return curIndex;
    }

    /**
     * 切换到指定tabId的tab
     */
    protected void selectByTabId(int tabId) {
        tabBar.selectByTabId(tabId);
    }
    //endregion

    //region 子类实现或重写

    /**
     * 根据tab信息，创建page fragment
     */
    protected abstract Fragment createTabPage(TabInfo tabInfo);

    /**
     * 返回要添加的tab信息集合
     */
    protected abstract ArrayList<TabInfo> tabToAdded();

    /**
     * 根据tab信息，判断是否需要预加载（未切换到该tab，就创建添加该tab的page）
     */
    protected abstract boolean needPreLoad(TabInfo tabInfo);

    /**
     * 是否可左右滑动
     */
    protected abstract boolean scrollable();

    /**
     * tabBar 每个tab的生成、选中、未选中 状态回调。
     * 1.可以在createTabView回调中自定义控制样式。
     * 2.在selectTabView和unSelectTabView更新tab选中和未选中的状态
     */
    protected TabBarOnCreateListener getCreateTabListener() {
        return new TabBarOnCreateListener() {
            @Override
            public View createTabView(TabBarView view, TabInfo tab, int atIndex) {
                TextView textView = new TextView(getActivity());
                DensityUtil.setTextSize(textView, R.dimen.txt_18);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.color_f3));
                textView.setText(tab.name);
                return textView;
            }

            @Override
            public void selectTabView(View view, TabInfo tabInfo) {
                if (view != null && view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.color_f1));
                }
            }

            @Override
            public void unSelectTabView(View view) {
                if (view != null && view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.color_f3));
                }
            }
        };
    }

    /**
     * tab切换，该方法可能handler.postDelayed 250毫秒执行。
     */
    protected void didSelectTab(TabInfo tab, TabInfo tabBefor) {

    }

    /**
     * 是否可以被选中
     */
    protected boolean selectable(int oldPage, int newPage) {
        return true;
    }

    protected View createDefaultFragView() {
        return scrollable() ? LayoutInflater.from(getActivity()).inflate(R.layout.frag_tabpage_scroll, null)
                : LayoutInflater.from(getActivity()).inflate(R.layout.frag_tabpage, null);
    }
    //endregion
}
