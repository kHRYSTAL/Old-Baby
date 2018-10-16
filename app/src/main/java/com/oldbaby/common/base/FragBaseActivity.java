package com.oldbaby.common.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oldbaby.R;
import com.oldbaby.common.util.statusbar.BarHide;
import com.oldbaby.common.util.statusbar.ImmersionBar;
import com.oldbaby.oblib.component.act.BaseFragmentActivity;
import com.oldbaby.oblib.component.act.TitleType;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.ScreenTool;
import com.oldbaby.oblib.view.title.OnTitleClickListener;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragBaseActivity extends BaseFragmentActivity implements OnTitleClickListener {

    protected LinearLayout rootLayout;
    protected RelativeLayout layoutForInfo;

    private TitleBarProxy titleBar;
    private DefaultTitleBarClickListener defaultTitleBarListener;
    private boolean isLogicDestroyed = false;
    private ImmersionBar immersionBar;

    public TitleBarProxy getTitleBar() {
        if (titleBar == null)
            initTitleBar();
        return titleBar;
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }

    @Override
    public void onContinueCreate(Bundle savedInstanceState) {
        super.onContinueCreate(savedInstanceState);
        immersionBar = ImmersionBar.with(this);
        buildFragView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        this.isLogicDestroyed = true;
        super.onDestroy();
        if (immersionBar != null)
            immersionBar.destroy();
    }

    private void buildFragView() {
        int customLayoutId = this.layResId();
        if (customLayoutId == 0) {
            View viewTitle = null;
            switch (titleType()) {
                case TitleType.TITLE_LAYOUT:
                    viewTitle = inflater.inflate(R.layout.titlebar_with_image, null);
                    break;
            }
            layoutForInfo = new RelativeLayout(this);
            layoutForInfo.setId(R.id.frag_container_rl);
            rootLayout = new LinearLayout(this);
            rootLayout.setId(R.id.frag_container);
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            // 顶部添加statusView 占位 如果设置隐藏状态栏 则该view显示
            RelativeLayout statusView = new RelativeLayout(this);
            statusView.setId(R.id.status_bar);
            RelativeLayout.LayoutParams sVParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                    .getDimensionPixelSize(R.dimen.status_bar_height)
            );
            sVParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            statusView.setLayoutParams(sVParams);
            rootLayout.addView(statusView);
            statusView.setVisibility(View.GONE);


            if (viewTitle != null) {
                viewTitle.setId(R.id.custom_title);
                RelativeLayout.LayoutParams vtParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getResources()
                        .getDimensionPixelSize(R.dimen.title_height));
                vtParams.addRule(RelativeLayout.BELOW, R.id.status_bar);
                rootLayout.addView(viewTitle, vtParams);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (viewTitle != null) {
                params.addRule(RelativeLayout.BELOW, R.id.custom_title);
            }
            layoutForInfo.addView(rootLayout, params);

            this.setContentView(layoutForInfo, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            this.setContentView(customLayoutId);
        }
        // 初始化titlebar
        initTitleBar();
    }

    private void initTitleBar() {
        titleBar = new TitleBarProxy();
        defaultTitleBarListener = new DefaultTitleBarClickListener(this);
        titleBar.configTitle(this.getWindow().getDecorView(), titleType(), this);
    }

    @Override
    public void onTitleClicked(View view, int tagId) {
        try {
            switch (tagId) {
                case TitleBarProxy.TAG_BACK:
                    defaultTitleBarListener.onBack();
                    break;
                default:
                    break;
            }
        } catch (final Throwable e) {
            MLog.e("FragBaseActivity", "exception happened", e);
        }
    }

    protected int layResId() {
        return 0;
    }

    @Override
    protected void configStartAnim(Intent intent) {
//        ComponentName comClass = intent.getComponent();
//        if (comClass == null)
//            return;
//        String className = comClass.getClassName();
//        if (className.equals(CommonFragActivity.class.getName())) {
//            className = CommonFragActivity.INCOME_FRAG_NAME;
//        }
//        AnimUtils.configAnim(this, className, true);
    }

    @Override
    public void finish() {
        super.finish();
        ScreenTool.HideInput(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int titleType() {
        return TitleType.TITLE_NONE;
    }

    public boolean isLogicDestroyed() {
        return isLogicDestroyed;
    }

    // 修改状态栏颜色
    @Override
    public void updateStatusBarColor(int resId) {
        View statusBar = findViewById(R.id.status_bar);
        statusBar.setBackgroundResource(resId);
        statusBar.setVisibility(View.VISIBLE);
        immersionBar.keyboardEnable(true).init();

    }

    // 修改标题栏颜色
    @Override
    public void updateTitleBarColor(int resId) {
        titleBar.getRootView().setBackgroundResource(resId);
        titleBar.hideBottomLine();
    }

    // 修改状态栏和标题栏颜色
    @Override
    public void updateTitleBarAndStatusBar(int resId) {
        updateStatusBarColor(resId);
        updateTitleBarColor(resId);
    }

    @Override
    public void hideStatusBar() {
        immersionBar.keyboardEnable(true).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init();
    }
}
