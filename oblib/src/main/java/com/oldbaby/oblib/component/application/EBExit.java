package com.oldbaby.oblib.component.application;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class EBExit {
    /**
     * 退出应用
     */
    public static final int TYPE_EXIT = 9527;

    private int type;

    public EBExit(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
