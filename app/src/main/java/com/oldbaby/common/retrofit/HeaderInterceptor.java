package com.oldbaby.common.retrofit;

import com.oldbaby.common.app.AppUtil;
import com.oldbaby.common.app.OldBabyApplication;
import com.oldbaby.common.app.PrefUtil;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.StringUtil;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * usage: retrofit 请求header
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest = addRequestHeader(request);
        Response response = chain.proceed(newRequest);
        return response;
    }

    private Request addRequestHeader(Request request) {
        Request result = request.newBuilder()
                .addHeader("device_id", AppUtil.Instance().getDeviceId())
                .addHeader("deviceModel", AppUtil.Instance().getDeviceModel())
                .addHeader("brand", AppUtil.Instance().getDeviceBrand())
                .addHeader("manufacturer", AppUtil.Instance().getDeviceManufacturer())
                .addHeader("os", "android")
                .addHeader("version", AppUtil.Instance().getVersionName())
                .addHeader("osVersion", android.os.Build.VERSION.RELEASE)
                .build();
        if (!StringUtil.isNullOrEmpty(PrefUtil.Instance().getToken())) {
            result = result.newBuilder()
                    .addHeader("uid", Long.toString(PrefUtil.Instance().getUserId()))
                    .addHeader("atk", PrefUtil.Instance().getToken())
                    .build();
        }
        String pageName = OldBabyApplication.getCurrentFragmentPageName();
        if (!StringUtil.isNullOrEmpty(pageName) && ((OGApplication) OGApplication.APP_CONTEXT).isAtFrontDesk()) {
            result = result.newBuilder()
                    .addHeader("pageId", pageName)
                    .build();
        }
        return result;
    }
}
