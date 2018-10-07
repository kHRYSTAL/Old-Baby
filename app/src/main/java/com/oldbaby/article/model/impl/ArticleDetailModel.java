package com.oldbaby.article.model.impl;

import com.oldbaby.article.model.IArticleDetailModel;
import com.oldbaby.article.model.remote.ArticleApi;
import com.oldbaby.common.bean.Article;
import com.oldbaby.common.retrofit.AppCall;
import com.oldbaby.common.retrofit.RetrofitFactory;

import retrofit.Call;
import retrofit.Response;
import rx.Observable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleDetailModel implements IArticleDetailModel {

    private ArticleApi api;

    public ArticleDetailModel() {
        api = RetrofitFactory.getInstance().getApi(ArticleApi.class);
    }

    @Override
    public Observable<Article> getArticleDetail(final String articleId) {
        return Observable.create(new AppCall<Article>() {
            @Override
            protected Response<Article> doRemoteCall() throws Exception {
                Call<Article> call = api.getArticleDetail(articleId);
                return call.execute();
            }
        });
    }
}
