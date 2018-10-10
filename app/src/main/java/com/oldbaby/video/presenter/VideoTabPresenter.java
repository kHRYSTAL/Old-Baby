package com.oldbaby.video.presenter;

import com.oldbaby.common.bean.Article;
import com.oldbaby.oblib.component.lifeprovider.PresenterEvent;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;
import com.oldbaby.oblib.mvp.presenter.pullrefresh.BasePullPresenter;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.video.model.VideoTabModel;
import com.oldbaby.video.view.IVideoTabView;

import rx.Subscriber;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/28
 * update time:
 * email: 723526676@qq.com
 */
public class VideoTabPresenter extends BasePullPresenter<Article, VideoTabModel, IVideoTabView> {

    private static final String TAG = VideoTabPresenter.class.getSimpleName();

    @Override
    protected void loadData(Integer nextId) {
        getVideoList(nextId, true);

    }

    private void getVideoList(Integer nextId, boolean isBackgroundTask) {
        model().getVideoList(nextId, 20, isBackgroundTask)
                .subscribeOn(getSchedulerSubscribe())
                .observeOn(getSchedulerObserver())
                .compose(this.<PageData<Article>>bindUntilEvent(PresenterEvent.UNBIND_VIEW))
                .subscribe(new Subscriber<PageData<Article>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view().onLoadFailed(e);
                        MLog.e(TAG, e, e.getMessage());
                    }

                    @Override
                    public void onNext(PageData<Article> videoPageData) {
                        view().onLoadSuccessfully(videoPageData);
                    }
                });
    }

    public void onClickItem(Article feed) {

    }

    public void onEmptyBtnClick() {

    }
}
