package com.oldbaby.video.model.remote;

import com.oldbaby.common.bean.Article;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/10
 * update time:
 * email: 723526676@qq.com
 */
public interface VideoApi {

    @GET("/video/")
    @Headers({"apiVersion:1.0"})
    Call<PageData<Article>> getVideoList(@Query("page") Integer next, @Query("page_size") int pageSize);
}
