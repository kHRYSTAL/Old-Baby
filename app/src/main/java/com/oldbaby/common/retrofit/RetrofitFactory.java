package com.oldbaby.common.retrofit;

import com.google.gson.Gson;
import com.oldbaby.BuildConfig;
import com.oldbaby.common.app.Config;
import com.oldbaby.common.retrofit.gson.GsonCreater;
import com.oldbaby.oblib.retrofit.RetrofitFactoryBase;
import com.squareup.okhttp.Interceptor;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class RetrofitFactory {

    RetrofitFactoryBase retrofitFactoryBase;

    private static class RetrofitHolder {
        // 静态初始化器 由JVM来保证线程安全
        private static RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    public static RetrofitFactory getInstance() {
        return RetrofitHolder.INSTANCE;
    }

    private RetrofitFactory() {
        retrofitFactoryBase = RetrofitFactoryBase.getInstance();
        Gson gson = GsonCreater.GreateGson();
        retrofitFactoryBase.setGson(gson);

        // 非release环境下需要忽略CA 否则https 请求失败
        // TODO: 18/9/29 未来区分环境后需要修改
        if (BuildConfig.DEBUG) {
            retrofitFactoryBase.ignoreCA();
        }
    }

    /**
     * 增加headerInterceptor
     *
     * @param interceptor
     */
    public void addHeaderInterceptor(Interceptor interceptor) {
        retrofitFactoryBase.addHeaderInterceptor(interceptor);
    }

    /**
     * 创建api代理
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getApi(Class<T> cls) {
        return retrofitFactoryBase.getApiService(Config.getRetrofitBaseUrl(), cls);
    }
}
