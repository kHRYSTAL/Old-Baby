package com.oldbaby.feed.presenter;

import com.oldbaby.common.bean.Article;
import com.oldbaby.feed.model.impl.FeedTabModel;
import com.oldbaby.feed.view.IFeedTabView;
import com.oldbaby.oblib.component.lifeprovider.PresenterEvent;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;
import com.oldbaby.oblib.mvp.presenter.pullrefresh.BasePullPresenter;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.gson.GsonHelper;

import rx.Subscriber;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public class FeedTabPresenter extends BasePullPresenter<Article, FeedTabModel, IFeedTabView> {

    private static final String TAG = FeedTabPresenter.class.getSimpleName();

    @Override
    protected void loadData(Integer nextId) {
        getArticleList(nextId, true);
    }

    private void getArticleList(final Integer nextId, final boolean isBackgroundTask) {
        model().getArticleList(nextId, 20, isBackgroundTask)
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
                    public void onNext(PageData<Article> articlePageData) {
                        view().onLoadSuccessfully(articlePageData);
                    }
                });
    }

    public void onClickArticleItem(String articleId) {
        view().jumpArticleDetail(articleId);
    }
}
