package com.oldbaby.tabhome.view.impl;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.oldbaby.R;
import com.oldbaby.common.base.FragBaseActivity;
import com.oldbaby.common.util.statusbar.ImmersionBar;
import com.oldbaby.oblib.component.act.TitleType;

import cn.jzvd.Jzvd;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class TabHome extends FragBaseActivity {

    private static final String TAG = TabHome.class.getSimpleName();

    FragTabHome fragTabHome;
    private long mExitTime;

    @Override
    protected int titleType() {
        return TitleType.TITLE_NONE;
    }

    @Override
    public void onContinueCreate(Bundle savedInstanceState) {
        super.onContinueCreate(savedInstanceState);
//        ImmersionBar.with(this).fullScreen(true).init();
        //TODO 在splash添加后处理intent操作与rxbus操作
        addFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //TODO 在splash添加后处理intent操作与rxbus操作
    }

    private void addFragment() {
        if (fragTabHome == null) {
            fragTabHome = new FragTabHome();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.frag_container, fragTabHome);
            ft.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragTabHome != null && fragTabHome.isAdded()) {
            fragTabHome.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Jzvd.goOnPlayOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.goOnPlayOnPause();
    }

    @Override
    public void onBackPressed() {
        // 如果为全屏播放 点击返回为退出全屏 不做任何操作
        if (Jzvd.backPress()) {
            return;
        }
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            //大于2000ms则认为是误操作，使用Toast进行提示
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            //并记录下本次点击“返回键”的时刻，以便下次进行判断
            mExitTime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }
}
