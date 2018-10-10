package com.oldbaby.video.model;

import com.oldbaby.common.bean.Article;
import com.oldbaby.common.model.PullMode;
import com.oldbaby.common.retrofit.AppCall;
import com.oldbaby.common.retrofit.RetrofitFactory;
import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;
import com.oldbaby.video.model.remote.VideoApi;

import retrofit.Call;
import retrofit.Response;
import rx.Observable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class VideoTabModel extends PullMode<Article> {

    private VideoApi api;

    public VideoTabModel() {
        api = RetrofitFactory.getInstance().getApi(VideoApi.class);

    }

    public Observable<PageData<Article>> getVideoList(final Integer nextId, final int pageSize, final boolean isBackgroundTask) {
        return Observable.create(new AppCall<PageData<Article>>() {
            @Override
            protected Response<PageData<Article>> doRemoteCall() throws Exception {
                setIsBackgroundTask(isBackgroundTask);
                Call<PageData<Article>> call = api.getVideoList(nextId, pageSize);
                return call.execute();
            }
        });
    }
}
