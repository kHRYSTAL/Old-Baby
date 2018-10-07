package com.oldbaby.video.presenter;

import com.oldbaby.common.bean.Feed;
import com.oldbaby.oblib.mvp.presenter.pullrefresh.BasePullPresenter;
import com.oldbaby.test.VideoConstant;
import com.oldbaby.video.model.VideoListModel;
import com.oldbaby.video.view.IVideoListView;

import java.util.ArrayList;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/28
 * update time:
 * email: 723526676@qq.com
 */
public class VideoListPresenter extends BasePullPresenter<Feed, VideoListModel, IVideoListView> {

    private static final String TAG = VideoListPresenter.class.getSimpleName();

    @Override
    protected void loadData(Integer nextId) {
        List<Feed> feeds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Feed feed = new Feed();
            feed.thumb = VideoConstant.videoThumbs[0][i];
            feed.title = VideoConstant.videoTitles[0][i];
            feed.url = VideoConstant.videoUrls[0][i];
            feeds.add(feed);
        }
        view().onLoadSuccessfully(feeds);
    }

    public void onClickItem(Feed feed) {

    }

    public void onEmptyBtnClick() {

    }
}
