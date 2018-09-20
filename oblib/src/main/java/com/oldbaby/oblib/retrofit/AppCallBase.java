package com.oldbaby.oblib.retrofit;

import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.MLog;
import com.squareup.okhttp.Headers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;

import retrofit.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * usage: 网络请求实现基类
 *  todo 需要确定django是否返回数据类型都为BasePyResp
 *  todo 需要确定django是否支持自定义错误码 > 200 && < 300认为是错误的
 *  todo 如果支持 错误信息在何处展现
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class AppCallBase<T> implements Observable.OnSubscribe<T> {

    // 是否为后台task
    private boolean isBackgroundTask = false;

    @Override
    public void call(Subscriber<? super T> subscriber) {

        Response<BasePyResp<T>> response = null;
        Throwable error = null;
        try {
            response = doRemoteCall();
        } catch (Exception e) {
            error = e;

        }

        try {
            handlerHeaders(response.headers());
        } catch (Exception e) {

        }

        if (subscriber.isUnsubscribed()) {
            //如果已取消订阅，不处理接下来的逻辑
            return;
        }

        if (response != null && response.isSuccess()) {
            subscriber.onNext(response.body().data);
            subscriber.onCompleted();
        } else {
            if (response != null) {
                int code = response.code();
                String errorBody = null;
                try {
                    errorBody = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ApiError apiError = new ApiError();
                apiError.code = code;
                apiError.body = errorBody;
                Headers headers = response.headers();
                if (headers != null && headers.size() > 0) {
                    String message = headers.get("msg");
                    if (message != null) {
                        try {
                            apiError.message = URLDecoder.decode(message, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                MLog.e("AppCallBase", code, apiError.message);
                handlerError(code, apiError.message);
                subscriber.onError(apiError);
            } else {
                if (!isBackgroundTask) {
                    if (error instanceof ConnectException) {
                        OGApplication.ShowToastFromBackground("无网络连接，请稍后重试");
                    } else {
                        OGApplication.ShowToastFromBackground("连接超时，请稍后重试");
                    }
                }
                subscriber.onError(error);
            }

        }

    }

    /**
     * 实际的请求操作，同步
     */
    protected abstract Response<BasePyResp<T>> doRemoteCall() throws Exception;

    /**
     * 处理错误异常
     */
    protected void handlerError(int code, String message) {
    }

    /**
     * 处理错误异常
     */
    protected void handlerHeaders(Headers headers) {
    }

    /**
     * 设置是否为后台task, 如果是后台task，则当接口访问失败后，不弹失败toast
     */
    protected final void setIsBackgroundTask(boolean isBack) {
        isBackgroundTask = isBack;
    }
}