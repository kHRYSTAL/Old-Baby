package com.oldbaby.profile.presenter;

import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.profile.model.IProfileTabModel;
import com.oldbaby.profile.view.IProfileTabView;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/17
 * update time:
 * email: 723526676@qq.com
 */
public class ProfileTabPresenter extends BasePresenter<IProfileTabModel, IProfileTabView> {

    public void onClickChange() {
        ToastUtil.showShort("暂不支持");
    }

    public void onClickCollect() {
        ToastUtil.showShort("暂不支持");
    }
}
