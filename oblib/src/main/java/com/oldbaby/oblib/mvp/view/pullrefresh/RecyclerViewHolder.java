package com.oldbaby.oblib.mvp.view.pullrefresh;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * usage: recyclerView holder
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    @Deprecated
    public abstract void recycle();
}
