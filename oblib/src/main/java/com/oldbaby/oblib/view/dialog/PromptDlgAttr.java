package com.oldbaby.oblib.view.dialog;

import android.support.annotation.DrawableRes;

/**
 * usage:PromptDlg属性
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class PromptDlgAttr {

    @DrawableRes
    public int resId;
    public String iconUrl;        //icon图片url
    public CharSequence title;
    public CharSequence subTitle;
    public String btnText;
    public boolean canceledOnTouchOutside = false;
    public boolean cancelable = true;
    public Integer btnBgResId;
    public boolean showClose = false;
    public CharSequence underBtnText;

    public boolean isTwoBtn = false;  //是否为底部两个按钮的样式，默认为一个按钮样式

    public String leftBtnText;
    public Integer leftBtnBgResId;
    public Integer leftBtnTextColorId;

    public String rightBtnText;
    public Integer rightBtnBgResId;
    public Integer rightBtnTextColorId;
}