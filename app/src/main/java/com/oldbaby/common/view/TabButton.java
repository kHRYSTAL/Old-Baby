package com.oldbaby.common.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oldbaby.R;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class TabButton extends RelativeLayout {

    public final ImageView icon;
    public final TextView text;
    public final ImageView ivRedDot;
    public final TextView tvUnreadMsgNum;

    public TabButton(Context context, int tabId) {
        super(context);

        icon = new ImageView(getContext());
        icon.setId(tabId);
        LayoutParams iconpl = new LayoutParams(
                DensityUtil.dip2px(20), DensityUtil.dip2px(20));
        iconpl.topMargin = DensityUtil.dip2px(7);
        iconpl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.addView(icon, iconpl);

        text = new TextView(getContext());
        LayoutParams textpl = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        DensityUtil.setTextSize(text, R.dimen.txt_10);
        text.setTextColor(0xFF8F9498);
        textpl.addRule(RelativeLayout.BELOW, tabId);
        textpl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textpl.topMargin = DensityUtil.dip2px(2);
        this.addView(text, textpl);

        ivRedDot = new ImageView(getContext());
        ivRedDot.setImageResource(R.drawable.red_dot);
        ivRedDot.setVisibility(View.GONE);
        LayoutParams redDotParams = new LayoutParams(
                DensityUtil.dip2px(8), DensityUtil.dip2px(8));
        redDotParams.rightMargin = DensityUtil.dip2px(-2);
        redDotParams.topMargin = DensityUtil.dip2px(-2);
        redDotParams.addRule(RelativeLayout.ALIGN_RIGHT, tabId);
        redDotParams.addRule(RelativeLayout.ALIGN_TOP, tabId);
        this.addView(ivRedDot, redDotParams);

        tvUnreadMsgNum = new TextView(getContext());
        tvUnreadMsgNum.setVisibility(View.GONE);
        tvUnreadMsgNum.setGravity(Gravity.CENTER);
        tvUnreadMsgNum.setTextColor(Color.WHITE);
        DensityUtil.setTextSize(tvUnreadMsgNum, R.dimen.txt_12);
        tvUnreadMsgNum.setBackgroundResource(R.drawable.reddot);
        tvUnreadMsgNum.setPadding(DensityUtil.dip2px(4), 0, DensityUtil.dip2px(4), 0);
        LayoutParams unreadMsgParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        unreadMsgParams.leftMargin = DensityUtil.dip2px(14);
        unreadMsgParams.topMargin = DensityUtil.dip2px(-5);
        unreadMsgParams.addRule(RelativeLayout.ALIGN_LEFT, tabId);
        unreadMsgParams.addRule(RelativeLayout.ALIGN_TOP, tabId);
        this.addView(tvUnreadMsgNum, unreadMsgParams);
    }

    public void showRedDot() {
        ivRedDot.setVisibility(View.VISIBLE);
    }

    public void hideRedDot() {
        ivRedDot.setVisibility(View.GONE);
    }

    /**
     * 显示未读消息数
     */
    public void showUnreadMsgNumber(int number) {
        int count = number;
        if (count > 0) {
            tvUnreadMsgNum.setVisibility(View.VISIBLE);
            tvUnreadMsgNum.setText(StringUtil.getRedDotCount(number));
        } else {
            tvUnreadMsgNum.setVisibility(View.GONE);
        }
    }
}
