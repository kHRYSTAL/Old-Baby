package com.oldbaby.oblib.view.tab;

import android.view.View;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public interface TabBarOnCreateListener {

    View createTabView(TabBarView view, TabInfo tab, int atIndex);

    void selectTabView(View view, TabInfo tabInfo);

    void unSelectTabView(View view);
}
