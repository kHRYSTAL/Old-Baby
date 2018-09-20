package com.oldbaby.oblib.view;

import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;

/**
 * 数据为空时的界面
 */
public class EmptyView extends LinearLayout {

    private static final int TXT_IMG_TOP_MARGIN = DensityUtil.dip2px(15);
    private static final int BTN_TXT_TOP_MARGIN = DensityUtil.dip2px(15);

    ImageView img;

    TextView tvPrompt;

    TextView tvBtn;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        img = new ImageView(getContext());
        LayoutParams imgParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        img.setLayoutParams(imgParams);
        addView(img);

        tvPrompt = new TextView(getContext());
        LayoutParams promptParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        promptParams.topMargin = TXT_IMG_TOP_MARGIN;
        tvPrompt.setLayoutParams(promptParams);
        tvPrompt.setGravity(Gravity.CENTER);
        tvPrompt.setLineSpacing(0, 1.1f);
        DensityUtil.setTextSize(tvPrompt, R.dimen.txt_16);
        tvPrompt.setTextColor(getResources().getColor(R.color.color_f3));
        addView(tvPrompt);

        tvBtn = new TextView(getContext());
        LayoutParams btnParams = new LayoutParams(
                DensityUtil.dip2px(150), DensityUtil.dip2px(40));
        btnParams.topMargin = BTN_TXT_TOP_MARGIN;
        tvPrompt.setLayoutParams(promptParams);
        tvBtn.setLayoutParams(btnParams);
        tvBtn.setVisibility(View.INVISIBLE);
        tvBtn.setGravity(Gravity.CENTER);
        tvBtn.setBackgroundResource(R.drawable.rect_bwhite_ssc_cmiddle);
        DensityUtil.setTextSize(tvBtn, R.dimen.txt_16);
        tvBtn.setTextColor(getResources().getColor(R.color.color_sc));
        addView(tvBtn);
    }

    public void setImgRes(int resId) {
        img.setImageResource(resId);
    }

    public void setPrompt(String txt) {
        tvPrompt.setText(txt);
    }

    public void setPrompt(Spannable spannable) {
        tvPrompt.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrompt.setHighlightColor(getResources().getColor(R.color.transparent));
        tvPrompt.setText(spannable);
    }

    public void setPromptTextColor(int colorResId) {
        tvPrompt.setTextColor(getResources().getColor(colorResId));
    }

    public void setBtnText(String txt) {
        tvBtn.setText(txt);
        tvBtn.setVisibility(StringUtil.isNullOrEmpty(txt) ? INVISIBLE : VISIBLE);
    }

    public void setBtnTextColor(int colorResId) {
        tvBtn.setTextColor(getResources().getColor(colorResId));
    }

    public void setBtnTextBackgroundResource(int resourceId) {
        tvBtn.setBackgroundResource(resourceId);
    }

    public void setBtnTextWidth(int width) {
        LayoutParams btnParams = new LayoutParams(
                width, LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = BTN_TXT_TOP_MARGIN;
        tvBtn.setLayoutParams(btnParams);
    }

    public void setBtnClickListener(OnClickListener listener) {
        tvBtn.setOnClickListener(listener);
    }

    public void setBtnVisibility(int visibility) {
        tvBtn.setVisibility(visibility);
    }

    public void setImgVisibility(int visibility) {
        img.setVisibility(visibility);
    }

    public void setImgLayoutParams(LayoutParams params) {
        img.setLayoutParams(params);
    }
}
