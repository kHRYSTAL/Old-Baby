package com.oldbaby.common.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oldbaby.R;

import cn.jzvd.JZDataSource;
import cn.jzvd.JzvdStd;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/16
 * update time:
 * email: 723526676@qq.com
 */
public class OBPlayerStd extends JzvdStd {

    private static final String TAG = OBPlayerStd.class.getSimpleName();

    private OnVideoPlayStateChangeListener listener;

    public LinearLayout mStartLayout;
    public LinearLayout mLayoutControl;

    public OBPlayerStd(Context context) {
        super(context);
    }

    public OBPlayerStd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mStartLayout = findViewById(R.id.start_layout);
        mLayoutControl = findViewById(R.id.layout_bottom);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_base;
    }

    // 在全屏播放时显示默认的标题栏 小屏时不显示
    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);
        RelativeLayout.LayoutParams retryParams = (RelativeLayout.LayoutParams) mRetryLayout.getLayoutParams();
        retryParams.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams startParams = (RelativeLayout.LayoutParams) mStartLayout.getLayoutParams();
        startParams.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams loadingParams = (RelativeLayout.LayoutParams) loadingProgressBar.getLayoutParams();
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            titleTextView.setVisibility(View.VISIBLE);
            retryParams.addRule(RelativeLayout.CENTER_VERTICAL);
            startParams.addRule(RelativeLayout.CENTER_VERTICAL);
            loadingParams.addRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            titleTextView.setVisibility(View.INVISIBLE);
            retryParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
            startParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
            loadingParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
        }
        mRetryLayout.setLayoutParams(retryParams);
        mStartLayout.setLayoutParams(startParams);
        loadingProgressBar.setLayoutParams(loadingParams);
    }

    // 替换播放器开始按钮图标
    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_pause_selector);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(INVISIBLE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(cn.jzvd.R.drawable.jz_click_replay_selector);
            replayTextView.setVisibility(VISIBLE);
        } else {
            startButton.setImageResource(R.drawable.img_play_btn);
            replayTextView.setVisibility(INVISIBLE);
        }
    }

    public void setOnVideoPlayStateChangeListener(OnVideoPlayStateChangeListener listener) {
        this.listener = listener;
    }

    // 暂停播放
    @Override
    public void onStatePause() {
        super.onStatePause();
        if (listener != null) {
            listener.onVideoPlayStateChange(VideoPlayState.STATE_PAUSE);
        }
    }

    // 暂停时隐藏所有control布局
    public void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_NORMAL:
            case SCREEN_WINDOW_LIST:
                setAllControlsVisiblity(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisiblity(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    // 开始播放
    @Override
    public void onStatePrepared() {
        super.onStatePrepared();
        if (listener != null) {
            listener.onVideoPlayStateChange(VideoPlayState.STATE_PLAY);
        }
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        if (listener != null) {
            listener.onVideoPlayStateChange(VideoPlayState.STATE_PLAY);
        }
    }

    @Override
    public void onStateError() {
        super.onStateError();
        if (listener != null)
            listener.onVideoPlayStateChange(VideoPlayState.STATE_ERROR);
    }

    // 完成播放
    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        if (listener != null) {
            listener.onVideoPlayStateChange(VideoPlayState.STATE_COMPLETE);
        }
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        if (listener != null) {
            listener.onVideoPlayStateChange(VideoPlayState.STATE_COMPLETE);
        }
    }
}
