package com.oldbaby.common.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldbaby.R;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgTwoBtnListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * usage: 提示框实现
 * author: kHRYSTAL
 * create time: 16/11/9
 * update time:
 * email: 723526676@qq.com
 */

public class PromptDialog<T> extends Dialog {

    @BindView(R.id.dlgIv)
    ImageView dlgIv;

    @BindView(R.id.dlgTitle)
    TextView dlgTitle;

    @BindView(R.id.dlgSubTitle)
    TextView dlgSubTitle;

    @BindView(R.id.dlgBtn)
    Button dlgBtn;

    @BindView(R.id.ivDlgClose)
    ImageView ivDlgClose;

    @BindView(R.id.tvDlgDesc)
    TextView tvDlgDesc;

    @BindView(R.id.llTwoBtn)
    LinearLayout llTwoBtn;

    @BindView(R.id.btnLeft)
    Button btnLeft;

    @BindView(R.id.btnRight)
    Button btnRight;

    private View root;
    private PromptDlgListener mListener;
    private PromptDlgTwoBtnListener mTwoBtnListener;
    private Context mContext;

    private T arg;
    private String tag;

    public PromptDialog(Context context) {
        super(context, R.style.PROGRESS_DIALOG);
        mContext = context;
        initView();
    }

    private void initView() {
        root = View.inflate(mContext, R.layout.ob_prompt_dialog, null);
        ButterKnife.bind(this, root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window dialogWindow = getWindow();
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        dialogWindow.getDecorView().setPadding(DensityUtil.dip2px(32), 0,
                DensityUtil.dip2px(32), 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.95f; // 透明度
        dialogWindow.setAttributes(lp);

        this.setContentView(root);
    }

    /**
     * 点击按钮回调 之前设置的tag与arg给监听器
     */
    @OnClick(R.id.dlgBtn)
    public void onButtonClick() {
        if (mListener != null) {
            mListener.onPromptClicked(mContext, tag, arg);
        } else {
            this.dismiss();
        }
    }

    @OnClick(R.id.ivDlgClose)
    public void onDlgClose() {
        this.dismiss();
    }

    /**
     * 点击左按钮回调 之前设置的tag与arg给监听器
     */
    @OnClick(R.id.btnLeft)
    public void onLeftButtonClick() {
        if (mTwoBtnListener != null) {
            mTwoBtnListener.onPromptLeftClicked(mContext, tag, arg);
        } else {
            this.dismiss();
        }
    }

    /**
     * 点击按钮回调 之前设置的tag与arg给监听器
     */
    @OnClick(R.id.btnRight)
    public void onRightButtonClick() {
        if (mTwoBtnListener != null) {
            mTwoBtnListener.onPromptRightClicked(mContext, tag, arg);
        } else {
            this.dismiss();
        }
    }

    /**
     * 设置本地图片至dialog
     *
     * @param resId
     */
    public void setImage(@DrawableRes int resId) {
        if (resId != 0) {
            dlgIv.setVisibility(View.VISIBLE);
            dlgIv.setImageResource(resId);
        } else {
            dlgIv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置drawable至dialog
     *
     * @param drawable
     */
    public void setImage(Drawable drawable) {
        if (drawable != null) {
            dlgIv.setVisibility(View.VISIBLE);
            dlgIv.setImageDrawable(drawable);
        } else {
            dlgIv.setVisibility(View.GONE);
        }
    }

    public void setImage(String imgUrl, int defaultId) {
        if (StringUtil.isNullOrEmpty(imgUrl) && defaultId == 0) {
            dlgIv.setVisibility(View.GONE);
        } else {
            dlgIv.setVisibility(View.VISIBLE);
            // glide 加载圆形图片

        }
    }

    /**
     * 设置主标题
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            dlgTitle.setVisibility(View.VISIBLE);
            dlgTitle.setText(title);
        } else {
            dlgTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 设置副标题 默认隐藏 如果有副标题则显示
     *
     * @param subTitle
     */
    public void setSubTitle(CharSequence subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            dlgSubTitle.setVisibility(View.VISIBLE);
            dlgSubTitle.setText(subTitle);
        } else {
            dlgSubTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 设置按钮文字
     *
     * @param buttonText
     */
    public void setButtonText(CharSequence buttonText) {
        dlgBtn.setText(buttonText);
    }

    /**
     * 设置按钮背景
     *
     * @param resId
     */
    public void setButtonBgResource(Integer resId) {
        if (resId != null) {
            dlgBtn.setBackgroundResource(resId);
        }
    }

    /**
     * 设置左按钮
     *
     * @param resId
     */
    public void setLeftBtn(String text, Integer resId, Integer colorId) {
        btnLeft.setText(text == null ? "" : text);
        if (resId != null) {
            btnLeft.setBackgroundResource(resId);
        }
        if (colorId != null) {
            btnLeft.setTextColor(mContext.getResources().getColor(colorId));
        }
    }

    /**
     * 设置右按钮
     *
     * @param resId
     */
    public void setRightBtn(String text, Integer resId, Integer colorId) {
        btnRight.setText(text == null ? "" : text);
        if (resId != null) {
            btnRight.setBackgroundResource(resId);
        }
        if (colorId != null) {
            btnRight.setTextColor(mContext.getResources().getColor(colorId));
        }
    }

    /**
     * 显示按钮下方文字 支持SpannableString
     */
    public void setDlgDesc(CharSequence charSequence) {
        if (charSequence != null && !StringUtil.isNullOrEmpty(charSequence.toString())) {
            tvDlgDesc.setVisibility(View.VISIBLE);
            tvDlgDesc.setText(charSequence);
        } else {
            tvDlgDesc.setVisibility(View.GONE);
        }
    }

    /**
     * 判断右上角关闭按钮是否显示
     */
    public void showCloseView(boolean show) {
        if (show) {
            ivDlgClose.setVisibility(View.VISIBLE);
        } else {
            ivDlgClose.setVisibility(View.GONE);
        }
    }

    /**
     * 获取主标题TextView
     *
     * @return
     */
    public TextView getTitle() {
        return dlgTitle;
    }

    /**
     * 获取副标题TextView
     *
     * @return
     */
    public TextView getSubTitle() {
        return dlgSubTitle;
    }

    /**
     * 获取ImageView
     *
     * @return
     */
    public ImageView getImageView() {
        return dlgIv;
    }

    /**
     * 获取按钮
     *
     * @return
     */
    public Button getButton() {
        return dlgBtn;
    }

    /**
     * 获取底部双按钮样式的布局
     *
     * @return
     */
    public LinearLayout getTwoBtnLayout() {
        return llTwoBtn;
    }

    /**
     * 设置传递的对象
     *
     * @param arg
     */
    public void setArg(T arg) {
        this.arg = arg;
    }

    /**
     * 获取传递的对象
     *
     * @return
     */
    public T getArg() {
        return arg;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setListener(PromptDlgListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    public void setTwoBtnListener(PromptDlgTwoBtnListener listener) {
        this.mTwoBtnListener = listener;
    }

    /**
     * 设置传递的Tag
     *
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}
