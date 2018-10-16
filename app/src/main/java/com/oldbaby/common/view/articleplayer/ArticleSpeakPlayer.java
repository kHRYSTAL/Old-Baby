package com.oldbaby.common.view.articleplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldbaby.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * usage: 文章详情页语音合成播放器
 * author: kHRYSTAL
 * create time: 18/10/16
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleSpeakPlayer extends FrameLayout {

    public static final int TYPE_PLAY = 1;
    public static final int TYPE_PAUSE = 2;

    private static final String TXT_PLAY = "开始";
    private static final String TXT_PAUSE = "暂停";

    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvPlay)
    TextView tvPlay;
    @BindView(R.id.tvSmall)
    TextView tvSmall;
    @BindView(R.id.tvBig)
    TextView tvBig;

    private int playType = TYPE_PLAY;

    private SpeakMediaPlayerAdapter mAdapter;

    public ArticleSpeakPlayer(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ArticleSpeakPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ArticleSpeakPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_article_float_player, null);
        ButterKnife.bind(this, view);
        addView(view);
        setPlayType(TYPE_PLAY);
    }

    /**
     * 设置播放按钮文字
     *
     * @param playType
     */
    public void setPlayType(int playType) {
        switch (playType) {
            case TYPE_PLAY:
                tvPlay.setText(TXT_PLAY);
                break;
            case TYPE_PAUSE:
                tvPlay.setText(TXT_PAUSE);
                break;
            default:
                break;
        }
    }

    public void setMediaPlayerAdapter(SpeakMediaPlayerAdapter adapter) {
        mAdapter = adapter;
    }

    @OnClick({R.id.ivBack, R.id.tvBack})
    public void goBack() {
        if (mAdapter != null)
            mAdapter.goBack();
    }

    @OnClick(R.id.tvPlay)
    public void onPlayClick() {
        if (mAdapter != null)
            mAdapter.onPlayClick();
    }

    @OnClick(R.id.tvSmall)
    public void onSmallClick() {
        if (mAdapter != null)
            mAdapter.changeTextToSmall();
    }

    @OnClick(R.id.tvBig)
    public void onBigClick() {
        if (mAdapter != null)
            mAdapter.changeTextToBig();
    }

}
