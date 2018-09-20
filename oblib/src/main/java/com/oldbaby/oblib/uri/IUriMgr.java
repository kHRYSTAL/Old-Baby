package com.oldbaby.oblib.uri;

import android.content.Context;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IUriMgr {
    void viewRes(Context context, String uriString);

    void router(Context context, String uriString);

    void router(Context context, String uriString, UriParam param);

    void router(Context context, String uriString, List<UriParam> params);

    void router(Context context, String uriString, List<UriParam> params, String from);
}
