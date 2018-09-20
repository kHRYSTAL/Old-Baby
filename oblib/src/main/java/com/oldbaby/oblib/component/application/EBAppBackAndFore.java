package com.oldbaby.oblib.component.application;

public class EBAppBackAndFore {

    /**
     * 从后台切到前台
     */
    public static final int TYPE_CUT_FOREGROUND = 1;

    /**
     * 从前台切到后台
     */
    public static final int TYPE_CUT_BACKGROUND = 2;

    private int type;
    private Object obj;

    public EBAppBackAndFore(int type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

    public int getType() {
        return type;
    }

    public Object getData() {
        return obj;
    }
}