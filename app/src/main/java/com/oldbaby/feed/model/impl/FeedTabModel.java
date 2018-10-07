package com.oldbaby.feed.model.impl;

import com.oldbaby.common.bean.Article;
import com.oldbaby.common.model.PullMode;
import com.oldbaby.common.retrofit.AppCall;
import com.oldbaby.common.retrofit.RetrofitFactory;
import com.oldbaby.feed.model.remote.FeedApi;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;

import retrofit.Call;
import retrofit.Response;
import rx.Observable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public class FeedTabModel extends PullMode<Article> {

    private FeedApi api;

    public FeedTabModel() {
        api = RetrofitFactory.getInstance().getApi(FeedApi.class);
    }

    public Observable<PageData<Article>> getArticleList(final Integer nextId, final int pageSize, final boolean isBackgroundTask) {
        return Observable.create(new AppCall<PageData<Article>>() {
            @Override
            protected Response<PageData<Article>> doRemoteCall() throws Exception {
                setIsBackgroundTask(isBackgroundTask);
                Call<PageData<Article>> call = api.getArticleList(nextId, pageSize);
                return call.execute();
            }
        });
    }
}
