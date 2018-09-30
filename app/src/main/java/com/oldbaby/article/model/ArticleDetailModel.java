package com.oldbaby.article.model;

import com.oldbaby.article.model.remote.ArticleApi;
import com.oldbaby.common.bean.ArticleData;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.common.retrofit.AppCall;
import com.oldbaby.common.retrofit.RetrofitFactory;
import com.oldbaby.oblib.retrofit.Result;

import java.util.List;

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
    public Observable<Result<ArticleData>> getArticleDetail(final String articleId) {
        return Observable.create(new AppCall<Result<ArticleData>>() {
            @Override
            protected Response<Result<ArticleData>> doRemoteCall() throws Exception {
                Call<Result<ArticleData>> call = api.getArticleDetail(articleId);
                return call.execute();
            }
        });
    }
}
