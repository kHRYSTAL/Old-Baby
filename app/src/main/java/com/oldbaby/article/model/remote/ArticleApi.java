package com.oldbaby.article.model.remote;

import com.oldbaby.common.bean.ArticleData;
import com.oldbaby.oblib.retrofit.Result;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public interface ArticleApi {

    @GET("/article/{articleId}/")
    @Headers({"apiVersion:1.0"})
    Call<Result<ArticleData>> getArticleDetail(@Path("articleId") String articleId);
}
