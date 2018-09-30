package com.oldbaby.common.retrofit;

import com.oldbaby.oblib.retrofit.AppCallBase;
import com.squareup.okhttp.Headers;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public abstract class AppCall<T> extends AppCallBase<T> {
    /**
     * 处理服务端响应的错误状态码
     */
    @Override
    protected void handlerError(int code, String message) {
        ApiErrorHandler.INSTANCE().handleApiError(code, message);
    }

    /**
     * 处理服务端响应的header头
     * @param headers
     */
    @Override
    protected void handlerHeaders(Headers headers) {
        // TODO: 18/9/29
    }

}
