package com.oldbaby.common.dto;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
public class CacheDao  extends BaseDaoImpl<CacheDto, Long> {

    /**
     * persistent a key-value
     *
     * @param key
     * @param value
     */
    public void set(String key, Serializable value) {

        if (StringUtil.isNullOrEmpty(key))
            return;

        CacheDto CacheDto = new CacheDto();
        CacheDto.key = key;
        CacheDto.value = value;
        try {
            this.createOrUpdate(CacheDto);
        } catch (Exception e) {
            MLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * get the persistent value
     *
     * @param key
     * @return
     */
    public Serializable get(String key) {
        if (StringUtil.isNullOrEmpty(key))
            return null;

        QueryBuilder<CacheDto, Long> qb = this.queryBuilder();
        CacheDto cache;
        try {
            cache = qb.where().eq(CacheDto.COL_KEY, key).queryForFirst();
            if (cache != null)
                return cache.value;
        } catch (Exception e) {
            MLog.e(TAG, e.getMessage(), e);
        }
        return null;

    }

    // ========constructors=========
    private static final String TAG = "dbmgr";

    public CacheDao(Class<CacheDto> dataClass) throws SQLException {
        super(dataClass);
    }

    public CacheDao(ConnectionSource connectionSource, Class<CacheDto> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    public CacheDao(ConnectionSource connectionSource,
                    DatabaseTableConfig<CacheDto> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

}
