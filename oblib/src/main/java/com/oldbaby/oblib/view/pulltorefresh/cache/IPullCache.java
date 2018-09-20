package com.oldbaby.oblib.view.pulltorefresh.cache;

import java.io.Serializable;

/**
 * 用来支持下拉刷新列表的缓存功能
 */
public interface IPullCache {

    /**
     * 获取指定的cache数据
     *
     * @return
     */
    public Object getCache(String cacheKey);

    /**
     * 缓存数据
     *
     */
    public void cacheData(String cacheKey, Serializable data);
}
