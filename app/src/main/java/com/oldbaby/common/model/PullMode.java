package com.oldbaby.common.model;

import com.oldbaby.common.dto.DBMgr;
import com.oldbaby.oblib.mvp.model.pullrefresh.IPullMode;
import com.oldbaby.oblib.mvp.view.pullrefresh.LogicIdentifiable;
import com.oldbaby.oblib.util.MLog;

import java.io.Serializable;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public abstract class PullMode<D extends LogicIdentifiable> implements IPullMode<D> {
    private static final String TAG = "PullMode";

    @Override
    public void saveRefreshTime() {
        DBMgr.getMgr().getCacheDao().set("refresh_time_" + getClass().getSimpleName(), System.currentTimeMillis());
    }

    @Override
    public long getLastRefreshTime() {
        try {
            return (Long) DBMgr.getMgr().getCacheDao().get("refresh_time_" + getClass().getSimpleName());
        } catch (Exception e) {
            MLog.e("PullMode", "getLastRefreshTime", e);
        }
        return 0;
    }

    @Override
    public void saveCache(List<D> cache) {
        try {
            DBMgr.getMgr().getCacheDao().set("cache_" + getClass().getSimpleName(), (Serializable) cache);
        } catch (Exception e) {
            MLog.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public List<D> getCache() {
        List<D> result = null;
        try {
            result = (List<D>) DBMgr.getMgr().getCacheDao().get("cache_" + getClass().getSimpleName());
        } catch (Exception e) {
            MLog.e(TAG, e.getMessage(), e);
        }
        return result;
    }
}
