package com.oldbaby.oblib.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
@Deprecated
public class Result<T> {

    @SerializedName("code")
    public int code;

    @SerializedName("msg")
    public String msg;

    @SerializedName("data")
    public T data;
}
