package com.oldbaby.oblib.mvp.model.pullrefresh;

import com.oldbaby.oblib.mvp.model.IMvpModel;
import com.oldbaby.oblib.mvp.view.pullrefresh.LogicIdentifiable;

import java.util.List;

/**
 * usage: 下拉刷新model
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IPullMode<D extends LogicIdentifiable> extends IMvpModel {
    /**
     * 保存刷新时间
     */
    void saveRefreshTime();

    /**
     * 获取上次刷新时间
     */
    long getLastRefreshTime();

    /**
     * 缓存上次刷新的数据
     */
    void saveCache(List<D> cache);

    /**
     * 获取上次刷新的数据缓存
     */
    List<D> getCache();
}
