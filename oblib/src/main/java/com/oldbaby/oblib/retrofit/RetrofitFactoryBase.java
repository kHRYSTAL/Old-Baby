package com.oldbaby.oblib.retrofit;

import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class RetrofitFactoryBase {

    private final OkHttpClient okHttpClient;
    private HashMap<Class, Object> apis = new HashMap<>();
    private Gson gson;

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private static class RetrofitHolder {
        // 静态初始化器，由JVM来保证线程安全
        private static RetrofitFactoryBase INSTANCE = new RetrofitFactoryBase();
    }

    public static RetrofitFactoryBase getInstance() {
        return RetrofitHolder.INSTANCE;
    }

    protected RetrofitFactoryBase() {
        okHttpClient = new OkHttpClient();
    }


    /**
     * 创建API代理
     */
    public <T> T getApiService(String baseUrl, Class<T> cls) {
        if (apis.containsKey(cls)) {
            return (T) apis.get(cls);
        }

        if (gson == null) {
            gson = new Gson();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ConverterFactory.create(gson))
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        T apiService = retrofit.create(cls);
        return apiService;
    }

    /**
     * 添加 header interceptor
     */
    public void addHeaderInterceptor(Interceptor interceptor) {
        if (okHttpClient != null && interceptor != null) {
            okHttpClient.interceptors().add(interceptor);
        }
    }

    /**
     * 创建API代理,带baseUrl
     */
    public <T> T getApi(String baseUrl, Class<T> cls) {
        return getApiService(baseUrl, cls);
    }

    /**
     * 忽略CA 证书
     */
    public void ignoreCA() {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
            if (okHttpClient != null) {
                okHttpClient.setSslSocketFactory(sc.getSocketFactory());
                okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}