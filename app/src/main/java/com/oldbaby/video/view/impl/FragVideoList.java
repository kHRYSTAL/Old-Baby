package com.oldbaby.video.view.impl;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.oldbaby.R;
import com.oldbaby.common.bean.Feed;
import com.oldbaby.common.util.SpiderHeader;
import com.oldbaby.oblib.mvp.view.pullrefresh.FragPullRecyclerView;
import com.oldbaby.oblib.mvp.view.pullrefresh.PullRecyclerViewAdapter;
import com.oldbaby.oblib.mvp.view.pullrefresh.RecyclerViewHolder;
import com.oldbaby.video.model.VideoListModel;
import com.oldbaby.video.presenter.VideoListPresenter;
import com.oldbaby.video.view.IVideoListView;

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
public class FragVideoList extends FragPullRecyclerView<Feed, VideoListPresenter> implements IVideoListView {

    public static final String PAGE_NAME = FragVideoList.class.getSimpleName();
    private VideoListPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // 划出屏幕停止播放
        internalView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.videoplayer);
                if (jzvd != null && jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
                    Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
                    if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
        // 自动播放
        internalView.addOnScrollListener(new AutoPlayScrollListener());
        return view;

    }

    @Override
    protected PullRecyclerViewAdapter makeAdapter() {
        PullRecyclerViewAdapter adapter = new PullRecyclerViewAdapter<ItemHolder>() {
            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_videoview, parent, false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(ItemHolder holder, int position) {
                holder.fill(getItem(position));
            }
        };
        return adapter;
    }

    @Override
    protected VideoListPresenter makePullPresenter() {
        presenter = new VideoListPresenter();
        presenter.setModel(new VideoListModel());
        return presenter;
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    class ItemHolder extends RecyclerViewHolder {

        @BindView(R.id.videoplayer)
        JzvdStd jzvdStd;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void fill(Feed feed) {
            jzvdStd.setUp(feed.url, feed.title, Jzvd.SCREEN_WINDOW_LIST);
            Glide.with(jzvdStd.getContext()).load(feed.thumb).into(jzvdStd.thumbImageView);
        }

        @Override
        public void recycle() {

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
                    JzvdStd homeGSYVideoPlayer = (JzvdStd) recyclerView.getChildAt(i).findViewById(R.id.videoplayer);
                    Rect rect = new Rect();
                    homeGSYVideoPlayer.getLocalVisibleRect(rect);
                    int videoheight = homeGSYVideoPlayer.getHeight();
                    if (rect.top == 0 && rect.bottom == videoheight) {
                        handleVideo(handleVideoTag, homeGSYVideoPlayer);
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
         * @param homeGSYVideoPlayer JZVideoPlayer播放器
         */
        private void handleVideo(VideoTagEnum handleVideoTag, JzvdStd homeGSYVideoPlayer) {
            switch (handleVideoTag) {
                case TAG_AUTO_PLAY_VIDEO:
                    if ((homeGSYVideoPlayer.currentState != JzvdStd.CURRENT_STATE_PLAYING)) {
                        // 进行播放
                        homeGSYVideoPlayer.startVideo();
                    }
                    break;
                case TAG_PAUSE_VIDEO:
                    if ((homeGSYVideoPlayer.currentState != JzvdStd.CURRENT_STATE_PAUSE)) {
                        // 模拟点击,暂停视频
                        homeGSYVideoPlayer.startButton.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
