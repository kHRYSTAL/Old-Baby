package com.oldbaby.video.view.impl;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oldbaby.R;
import com.oldbaby.common.bean.Article;
import com.oldbaby.common.media.OBPlayerStd;
import com.oldbaby.common.media.OnVideoPlayStateChangeListener;
import com.oldbaby.common.media.VideoPlayState;
import com.oldbaby.oblib.mvp.view.pullrefresh.FragPullRecyclerView;
import com.oldbaby.oblib.mvp.view.pullrefresh.PullRecyclerViewAdapter;
import com.oldbaby.oblib.mvp.view.pullrefresh.RecyclerViewHolder;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.video.model.VideoTabModel;
import com.oldbaby.video.presenter.VideoTabPresenter;
import com.oldbaby.video.view.IVideoTabView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class FragVideoTab extends FragPullRecyclerView<Article, VideoTabPresenter> implements IVideoTabView {

    public static final String PAGE_NAME = FragVideoTab.class.getSimpleName();
    private VideoTabPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater
                .inflate(R.layout.frag_tab_item, container, false);
        ((LinearLayout) rootView.findViewById(R.id.llContainer)).addView(super.onCreateView(inflater, container, savedInstanceState),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ButterKnife.bind(this, rootView);
        initView();

        // 划出屏幕停止播放
        internalView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                OBPlayerStd obvd = view.findViewById(R.id.videoplayer);
                if (obvd != null && obvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
                    Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
                    if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
        // 自动播放
        internalView.addOnScrollListener(new AutoPlayScrollListener());
        return rootView;

    }

    private void initView() {
        internalView.setPadding(0, DensityUtil.dip2px(60), 0, 0);
        internalView.setClipChildren(false);
        internalView.setClipToPadding(false);
    }

    @Override
    protected PullRecyclerViewAdapter makeAdapter() {
        PullRecyclerViewAdapter adapter = new PullRecyclerViewAdapter<ItemHolder>() {
            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_video, parent, false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(ItemHolder holder, int position) {
                holder.fill(getItem(position), position);
            }
        };
        return adapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pullView.setBackgroundColor(getResources().getColor(R.color.transparent));
        internalView.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    protected VideoTabPresenter makePullPresenter() {
        presenter = new VideoTabPresenter();
        presenter.setModel(new VideoTabModel());
        return presenter;
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    class ItemHolder extends RecyclerViewHolder implements OnVideoPlayStateChangeListener {

        @BindView(R.id.videoplayer)
        OBPlayerStd videoplayer;
        @BindView(R.id.tvVideoTitle)
        TextView tvVideoTitle;
        @BindView(R.id.tvVideoSource)
        TextView tvVideoSource;
        @BindView(R.id.tvVideoTime)
        TextView tvVideoTime;
        @BindView(R.id.rlVideoContainer)
        RelativeLayout rlVideoContainer;

//        private int pos;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            videoplayer.setOnVideoPlayStateChangeListener(this);
        }

        public void fill(Article article, int position) {
//            this.pos = position;
            if (article != null) {
                videoplayer.setUp(article.videoUrl, article.title, Jzvd.SCREEN_WINDOW_LIST);
                Glide.with(videoplayer.getContext()).load(article.thumbPicUrl).into(videoplayer.thumbImageView);
                if (!StringUtil.isNullOrEmpty(article.title))
                    tvVideoTitle.setText(article.title);
                if (!StringUtil.isNullOrEmpty(article.source))
                    tvVideoSource.setText(String.format("来自%s", article.source));
                if (!StringUtil.isNullOrEmpty(article.pbTime))
                    tvVideoTime.setText(article.pbTime);
                rlVideoContainer.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void recycle() {

        }

        @Override
        public void onVideoPlayStateChange(int state) {
            switch (state) {
                case VideoPlayState.STATE_PLAY:
                    rlVideoContainer.setVisibility(View.GONE);
                    break;
                case VideoPlayState.STATE_COMPLETE:
                case VideoPlayState.STATE_PAUSE:
                    rlVideoContainer.setVisibility(View.VISIBLE);
                    break;

            }
        }
    }

    /**
     * 监听recycleView滑动状态，
     * 自动播放可见区域内的第一个视频
     */
    private static class AutoPlayScrollListener extends RecyclerView.OnScrollListener {
        private int firstVisibleItem = 0;
        private int lastVisibleItem = 0;
        private int visibleCount = 0;

        /**
         * 被处理的视频状态标签
         */
        private enum VideoTagEnum {
            /**
             * 自动播放视频
             */
            TAG_AUTO_PLAY_VIDEO,
            /**
             * 暂停视频
             */
            TAG_PAUSE_VIDEO
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    autoPlayVideo(recyclerView, VideoTagEnum.TAG_AUTO_PLAY_VIDEO);
                default:
                    // 滑动时暂停视频 autoPlayVideo(recyclerView, VideoTagEnum.TAG_PAUSE_VIDEO);
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                firstVisibleItem = linearManager.findFirstVisibleItemPosition();
                lastVisibleItem = linearManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
            }
        }

        /**
         * 循环遍历 可见区域的播放器
         * 然后通过 getLocalVisibleRect(rect)方法计算出哪个播放器完全显示出来
         * <p>
         * getLocalVisibleRect相关链接：http://www.cnblogs.com/ai-developers/p/4413585.html
         *
         * @param recyclerView
         * @param handleVideoTag 视频需要进行状态
         */
        private void autoPlayVideo(RecyclerView recyclerView, VideoTagEnum handleVideoTag) {
            for (int i = 0; i < visibleCount; i++) {
                if (recyclerView != null && recyclerView.getChildAt(i) != null && recyclerView.getChildAt(i).findViewById(R.id.videoplayer) != null) {
                    OBPlayerStd obPlayerStd = (OBPlayerStd) recyclerView.getChildAt(i).findViewById(R.id.videoplayer);
                    Rect rect = new Rect();
                    obPlayerStd.getLocalVisibleRect(rect);
                    int videoheight = obPlayerStd.getHeight();
                    if (rect.top == 0 && rect.bottom == videoheight) {
                        handleVideo(handleVideoTag, obPlayerStd);
                        // 跳出循环，只处理可见区域内的第一个播放器
                        break;
                    }
                }
            }
        }

        /**
         * 视频状态处理
         *
         * @param handleVideoTag     视频需要进行状态
         * @param obPlayerStd JZVideoPlayer播放器
         */
        private void handleVideo(VideoTagEnum handleVideoTag, OBPlayerStd obPlayerStd) {
            switch (handleVideoTag) {
                case TAG_AUTO_PLAY_VIDEO:
                    if ((obPlayerStd.currentState != JzvdStd.CURRENT_STATE_PLAYING)) {
                        // 进行播放
                        obPlayerStd.startVideo();
                    }
                    break;
                case TAG_PAUSE_VIDEO:
                    if ((obPlayerStd.currentState != JzvdStd.CURRENT_STATE_PAUSE)) {
                        // 模拟点击,暂停视频
                        obPlayerStd.startButton.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
