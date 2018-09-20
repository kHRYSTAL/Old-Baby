package com.oldbaby.oblib.view.dialog;

import android.support.annotation.DrawableRes;

/**
 * usage: TipsDialog 资源属性配置
 * author: kHRYSTAL
 * create time: 17/7/12
 * update time:
 * email: 723526676@qq.com
 */

public class TipsDlgAttr {
    @DrawableRes
    public int headerImg;
    @DrawableRes
    public int closeImg;
    public CharSequence desc;
    public boolean canceledOnTouchOutside = false;
    public boolean cancelable = true;
}
