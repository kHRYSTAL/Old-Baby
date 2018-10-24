package com.oldbaby.profile.presenter;

import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.profile.model.IProfileTabModel;
import com.oldbaby.profile.view.IProfileTabView;

import skin.support.SkinCompatManager;
import skin.support.utils.SkinPreference;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/17
 * update time:
 * email: 723526676@qq.com
 */
public class ProfileTabPresenter extends BasePresenter<IProfileTabModel, IProfileTabView> {

    private static final String TAG = ProfileTabPresenter.class.getSimpleName();
    private static final String NIGHT_SKIN_NAME = "night.skin";

    private SkinCompatManager.SkinLoaderListener skinLoaderListener;

    public ProfileTabPresenter() {
        skinLoaderListener = new SkinCompatManager.SkinLoaderListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                updateView();
            }

            @Override
            public void onFailed(String errMsg) {
                updateView();

            }
        };
    }

    @Override
    protected void updateView() {
        super.updateView();
        view().setThemeBtnText(
                String.format("%s间模式", SkinPreference.getInstance().getSkinName().equals(NIGHT_SKIN_NAME) ? "日" : "夜"));
    }


    public void onClickChange() {
        if (SkinPreference.getInstance().getSkinName().equals(NIGHT_SKIN_NAME))
            SkinCompatManager.getInstance().loadSkin("", skinLoaderListener, SkinCompatManager.SKIN_LOADER_STRATEGY_NONE);
        else
            SkinCompatManager.getInstance().loadSkin(NIGHT_SKIN_NAME, skinLoaderListener, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
    }

    public void onClickCollect() {
        ToastUtil.showShort("暂不支持");
    }
}
