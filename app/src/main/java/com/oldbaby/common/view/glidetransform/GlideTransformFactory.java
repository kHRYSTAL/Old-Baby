package com.oldbaby.common.view.glidetransform;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/17
 * update time:
 * email: 723526676@qq.com
 */
public class GlideTransformFactory {

    public static RequestOptions getCircleOptions() {
        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
        return mRequestOptions;
    }
}
