package com.oldbaby.oblib.view.dialog;

import android.support.annotation.DrawableRes;

/**
 * usage: MultiBtnDlg 的属性
 * author: kHRYSTAL
 * create time: 17/7/13
 * update time:
 * email: 723526676@qq.com
 */

public class MultiBtnDlgAttr {
    @DrawableRes
    public int resId;
    public String iconUrl;        //icon图片url
    public CharSequence title;
    public CharSequence subTitle;
    public String btnOneText;
    public int btnOneBgResId;
    public String btnTwoText;
    public int btnTwoBgResId;
    public boolean showClose = false;
    public boolean canceledOnTouchOutside = false;
    public boolean cancelable = true;
}
