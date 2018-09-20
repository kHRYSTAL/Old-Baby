package com.oldbaby.oblib.view.pulltorefresh;

public interface PullRefeshListener {

    void loadNormal();

    void loadMore(String nextId);

}
