package com.oldbaby.oblib.component.adapter;

/**
 * when adapter changed, use this listener to notify
 */
public interface OnAdapterChangeListener {

    /**
     * when list adapter, count is item count, when section list adapter, count
     * is group count
     *
     * @param count
     */
    void onDataChanged(int count);

}