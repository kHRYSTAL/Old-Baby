package com.oldbaby.common.dto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.oldbaby.R;
import com.oldbaby.common.app.OldBabyApplication;
import com.oldbaby.oblib.util.MLog;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class DBMgr extends OrmLiteSqliteOpenHelper {

    private CacheDao cacheDao;
    private TrackerDao trackerDao;

    public TrackerDao getTrackerDao() {
        if (trackerDao == null) {
            try {
                trackerDao = getDao(TrackerDto.class);
            } catch (SQLException e) {
                MLog.e(TAG, e.getMessage(), e);
            }
        }
        return trackerDao;
    }

    public CacheDao getCacheDao() {
        if (cacheDao == null) {
            try {
                cacheDao = getDao(CacheDto.class);
            } catch (SQLException e) {
                MLog.e(TAG, e.getMessage(), e);
            }
        }
        return cacheDao;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        createTable(arg1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource arg1, int before,
                          int current) {
        // 做数据库升级操作
        DBUpdateUtil.getInstance().update(db, arg1);
        updateData(before);
    }

    public void createTable(ConnectionSource cs) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, CacheDto.class);
        } catch (SQLException e) {
            MLog.e(TAG, "create table", e);
        }
    }

    /**
     * 数据库更新后的后续操作，如数据转移。
     */
    private void updateData(final int before) {
        new Thread(new Runnable() {

            @Override
            public void run() {

            }
        }).start();
    }

    // =========================

    private static final String TAG = "dbmgr";
    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private static DBMgr helper = null;

    /**
     * 增加课节播放状态
     */
    private static final int DB_VERSION_MILESTONE = 1;


    // 当前版本的DB version
    private static final int DB_VERSION = DB_VERSION_MILESTONE;

    public static DBMgr getMgr() {
        return getHelper(OldBabyApplication.APP_CONTEXT);
    }

    /**
     * Get the helper, possibly constructing it if necessary. For each call to
     * this method, there should be 1 and only 1 call to {@link #close()}.
     */
    private static synchronized DBMgr getHelper(Context context) {
        if (helper == null) {
            helper = new DBMgr(context);
        }

        usageCounter.incrementAndGet();
        return helper;
    }

    /**
     * for data base helper release.
     */
    public static synchronized void release() {
        if (helper != null) {
            usageCounter.set(1);
            helper.close();
            helper = null;
        }
    }

    /**
     * Close the database connections and clear any cached DAOs. For each call
     * to {@link #getHelper(Context)}, there should be 1 and only 1 call to this
     * method. If there were 3 calls to {@link #getHelper(Context)} then on the
     * 3rd call to this method, the helper and the underlying database
     * connections will be closed.
     */
    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            helper = null;
        }
    }

    private DBMgr(Context context) {
        super(context, buildDBName(), null, DB_VERSION, R.raw.ormlite_config);
    }

    public static String buildDBName() {
        String dbName = String.format("zhisland.db");
        MLog.d(TAG, dbName);
        return dbName;
    }
}