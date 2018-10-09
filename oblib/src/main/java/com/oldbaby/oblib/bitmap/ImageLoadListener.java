package com.oldbaby.oblib.bitmap;

public interface ImageLoadListener {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 0;

    /**
     * 1: success, 0:fail
     */
    void onLoadFinished(String url, int status);

}