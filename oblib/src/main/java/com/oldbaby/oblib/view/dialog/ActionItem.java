package com.oldbaby.oblib.view.dialog;

public class ActionItem {

    public int id;
    public String name;
    public String uri;
    // 图片resId
    public int resId;
    // 文字颜色
    public Integer textColor;
    public Object tag;

    public ActionItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ActionItem(int id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public ActionItem(int id, String name, int resId) {
        this.id = id;
        this.name = name;
        this.resId = resId;
    }

    public ActionItem(int id, int textColor, String name) {
        this.id = id;
        this.name = name;
        this.textColor = textColor;
    }
}