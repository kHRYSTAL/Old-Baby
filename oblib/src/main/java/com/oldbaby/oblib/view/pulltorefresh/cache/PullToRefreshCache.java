package com.oldbaby.oblib.view.pulltorefresh.cache;

import java.io.Serializable;

/**
 * 用来管理、缓存下拉刷新中的内容数据，nextId，最近一次的刷新时间等
 *
 * @author arthur
 */
public class PullToRefreshCache {

    private static IPullCache pullCacheUtil;
    private static final long MIN_INTERVAL = 5 * 60 * 1000;

    // 缓存的key
    private String refreshKey;
    // 两次刷新之间的最小时间间隔
    private long interval = MIN_INTERVAL;

    private boolean autoRefresh = true;

    public PullToRefreshCache(String cacheKey) {
        this.refreshKey = cacheKey;
    }

    /**
     * 根据指定的refreshkey，判断其是否已经超出指定的时间范围
     *
     * @return
     */
    public boolean isOutRefreshInterval() {

        long lastRefreshTime = getLastRefreshTime();
        long interval = System.currentTimeMillis() - lastRefreshTime;

        return autoRefresh && (interval > this.interval);
    }

    private long getLastRefreshTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 设置两次自动刷新之间的间隔时间
     *
     * @param interval
     */
    public void setRefreshIntrval(long interval) {
        this.interval = interval;
    }

    /**
     * 设置是否需要自动刷新
     *
     * @param autoRefresh
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    /**
     * 获取指定的cache数据
     *
     * @return
     */
    public Object getCache() {
        if (pullCacheUtil == null)
            return null;
        return pullCacheUtil.getCache(refreshKey);
    }

    /**
     * 缓存数据
     *
     * @param newCacheData
     */
    public void cacheData(Serializable newCacheData) {
        pullCacheUtil.cacheData(refreshKey, newCacheData);
    }

    /**
     * 设置下拉刷新的缓存接口
     *
     * @param pullCache
     */
    public static void setCacheUtil(IPullCache pullCache) {
        pullCacheUtil = pullCache;
    }

}
