package com.oldbaby.common.adapter;

import com.oldbaby.oblib.image.viewer.ImageDataAdapter;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/8
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleImageAdapter implements ImageDataAdapter {

    List<String> urls;

    public ArticleImageAdapter(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public int count() {
        return urls.size();
    }

    @Override
    public String getUrl(int postion) {
        return urls.get(postion);
    }

    @Override
    public int getDrawableId(int position) {
        return 0;
    }

    @Override
    public void remove(int position) {

    }

    @Override
    public String getDesc(int position) {
        return null;
    }
}
