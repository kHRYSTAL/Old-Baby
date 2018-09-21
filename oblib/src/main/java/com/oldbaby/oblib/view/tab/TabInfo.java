package com.oldbaby.oblib.view.tab;

import java.io.Serializable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public class TabInfo implements Serializable {

    private static final long serialVersionUID = 5634391684162698185L;

    public String name;
    public int tabId;
    public int arg1;
    public Object tag;

    public TabInfo(String name, int tabId) {
        this(name, tabId, null);
    }

    public TabInfo(String name, int tabId, Object tag) {
        this.name = name;
        this.tabId = tabId;
        this.tag = tag;
    }

    public TabInfo(String name, int tabId, int arg1, Object tag) {
        this.name = name;
        this.tabId = tabId;
        this.arg1 = arg1;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return name;
    }
}
