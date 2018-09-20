package com.oldbaby.oblib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.util.DensityUtil;

/**
 * 网络错误时的view
 */
public class NetErrorView extends LinearLayout {

    private static final int TXT_IMG_TOP_MARGIN = DensityUtil.dip2px(15);
    private static final int BTN_TXT_TOP_MARGIN = DensityUtil.dip2px(15);

    ImageView img;

    TextView tvPrompt;

    TextView tvBtn;

    public NetErrorView(Context context) {
        this(context, null);
    }

    public NetErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setImgRes(R.drawable.img_empty_nowifi);
        addView(img);

        tvPrompt = new TextView(getContext());
        LayoutParams promptParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        promptParams.topMargin = TXT_IMG_TOP_MARGIN;
        tvPrompt.setLayoutParams(promptParams);
        tvPrompt.setGravity(Gravity.CENTER);
        tvPrompt.setLineSpacing(0, 1.1f);
        DensityUtil.setTextSize(tvPrompt, R.dimen.txt_16);
        tvPrompt.setTextColor(getResources().getColor(R.color.color_f2));
        tvPrompt.setText("哎呦，网络连接不畅");
        addView(tvPrompt);

        tvBtn = new TextView(getContext());
        LayoutParams btnParams = new LayoutParams(
                DensityUtil.dip2px(150), DensityUtil.dip2px(40));
        btnParams.topMargin = BTN_TXT_TOP_MARGIN;
        tvPrompt.setLayoutParams(promptParams);
        tvBtn.setLayoutParams(btnParams);
        tvBtn.setGravity(Gravity.CENTER);
        tvBtn.setBackgroundResource(R.drawable.rect_bwhite_ssc_cmiddle);
        DensityUtil.setTextSize(tvBtn, R.dimen.txt_16);
        tvBtn.setTextColor(getResources().getColor(R.color.color_sc));
        tvBtn.setText("点击重新加载");
        addView(tvBtn);
    }

    /**
     * 重新加载按钮 listener
     */
    public void setBtnReloadClickListener(OnClickListener listener) {
        tvBtn.setOnClickListener(listener);
    }

    /**
     * 设置图标
     */
    public void setImgRes(int resId) {
        img.setImageResource(resId);
    }
}