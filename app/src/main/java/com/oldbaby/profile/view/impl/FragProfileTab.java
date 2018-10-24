package com.oldbaby.profile.view.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oldbaby.R;
import com.oldbaby.common.view.glidetransform.GlideTransformFactory;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.FragBaseMvps;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.profile.model.impl.ProfileTabModel;
import com.oldbaby.profile.presenter.ProfileTabPresenter;
import com.oldbaby.profile.view.IProfileTabView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/17
 * update time:
 * email: 723526676@qq.com
 */
public class FragProfileTab extends FragBaseMvps implements IProfileTabView {

    private static final String TAG = FragProfileTab.class.getSimpleName();
    private static final String PAGE_NAME = FragProfileTab.class.getSimpleName();

    @BindView(R.id.ivUserAvatar)
    ImageView ivUserAvatar;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvUserMobile)
    TextView tvUserMobile;
    @BindView(R.id.tvCollect)
    TextView tvCollect;
    @BindView(R.id.rlCollect)
    RelativeLayout rlCollect;
    @BindView(R.id.btnChange)
    Button btnChange;
    @BindView(R.id.ivBackground)
    ImageView ivBackground;

    private ProfileTabPresenter presenter;

    @Override
    protected Map<String, BasePresenter> createPresenters() {
        Map<String, BasePresenter> presenterMap = new HashMap<>();
        presenter = new ProfileTabPresenter();
        presenter.setModel(new ProfileTabModel());
        presenterMap.put(ProfileTabPresenter.class.getSimpleName(), presenter);
        return presenterMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_tab_item, container, false);
        LinearLayout llContainer = rootView.findViewById(R.id.llContainer);
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        // 此处可以使用holder
        llContainer.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        // 替换背景图片
        ivBackground.setBackgroundResource(R.drawable.tab_background_profile);
        // 加载头像
        Glide.with(getActivity()).load(R.drawable.default_avatar).apply(GlideTransformFactory.getCircleOptions()).into(ivUserAvatar);
        tvUserName.setText("一个汽车维修员");
        tvUserMobile.setText("15222591730");
    }

    @OnClick(R.id.rlCollect)
    public void onClickCollect() {
        presenter.onClickCollect();
    }

    @OnClick(R.id.btnChange)
    public void onClickChange() {
        presenter.onClickChange();
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    @Override
    public void setThemeBtnText(String text) {
        if (!StringUtil.isNullOrEmpty(text))
            btnChange.setText(text);
    }
}
