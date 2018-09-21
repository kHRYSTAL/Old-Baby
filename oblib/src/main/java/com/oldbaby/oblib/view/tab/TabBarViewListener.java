package com.oldbaby.oblib.view.tab;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public interface TabBarViewListener {

    public boolean shouldSelectTab(TabBarView view, TabInfo tab, int atIndex);

    public void didSelectTabBar(TabBarView view, TabInfo tab, int atIndex);
}
