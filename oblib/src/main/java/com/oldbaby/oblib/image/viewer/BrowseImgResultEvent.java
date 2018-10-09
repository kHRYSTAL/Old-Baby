package com.oldbaby.oblib.image.viewer;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/8
 * update time:
 * email: 723526676@qq.com
 */
public class BrowseImgResultEvent {
    private String requestNonce;
    private List<String> deleteUrls;

    public BrowseImgResultEvent(String requestNonce, List<String> deleteUrls) {
        this.requestNonce = requestNonce;
        this.deleteUrls = deleteUrls;
    }

    public String getRequestNonce() {
        return requestNonce;
    }

    public List<String> getDeleteUrls() {
        return deleteUrls;
    }
}
