package com.oldbaby.profile.view;

import com.oldbaby.oblib.mvp.view.IMvpView;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/17
 * update time:
 * email: 723526676@qq.com
 */
public interface IProfileTabView extends IMvpView {
    void setThemeBtnText(String text);

    void showSpeechPersonDialog();
}
