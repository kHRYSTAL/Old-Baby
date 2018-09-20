package com.oldbaby.oblib.retrofit;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class ApiError extends Throwable{
    public int code;
    public String body;
    public String message;
}
