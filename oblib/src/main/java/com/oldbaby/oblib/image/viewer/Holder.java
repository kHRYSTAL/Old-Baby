package com.oldbaby.oblib.image.viewer;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.oldbaby.oblib.bitmap.ImageLoadListener;
import com.oldbaby.oblib.view.ImageViewEx;

public class Holder implements ImageLoadListener {

    public ImageViewEx image;
    public ImageViewEx simage;
    public ProgressBar pro;
    public TextView tvDesc;
    public ScrollView svImgViewerDesc;

    @Override
    public void onLoadFinished(String url, int status) {
        pro.setVisibility(View.GONE);
        if (status == ImageLoadListener.STATUS_SUCCESS) {
            simage.setVisibility(View.GONE);
        } else {
            image.setImageDrawable(simage.getDrawable());
            simage.setVisibility(View.GONE);
        }

    }

    public void setDescVisible(int descVisible) {
        if (descVisible > 0) {
            svImgViewerDesc.setVisibility(View.VISIBLE);
        } else {
            svImgViewerDesc.setVisibility(View.GONE);
        }
    }
}