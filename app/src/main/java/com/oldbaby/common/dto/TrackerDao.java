package com.oldbaby.common.dto;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.gson.GsonHelper;
import com.oldbaby.tracker.bean.TrackerEvent;
import com.oldbaby.tracker.model.IStore;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrackerDao extends BaseDaoImpl<TrackerDto, Long> implements IStore {

    private static final String TAG = "tracker";

    @Override
    public void log(TrackerEvent se) {
        TrackerDto dto = new TrackerDto(se);
        try {
            this.createOrUpdate(dto);
        } catch (SQLException e) {
            MLog.e(TAG, e.getLocalizedMessage(), e);
        } catch (IllegalStateException e) {
            MLog.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<TrackerEvent> getUploadEvents(int maxUploads) {
        try {
            QueryBuilder<TrackerDto, Long> builder = this.queryBuilder();
            builder.orderBy(TrackerDto.COL_CTIME, true);
            builder.limit(maxUploads);
            List<TrackerDto> dtos = builder.query();
            if (dtos == null || dtos.isEmpty()) {
                return null;
            }
            List<TrackerEvent> events = new ArrayList<>(dtos.size());
            for (TrackerDto dto : dtos) {
                TrackerEvent event = GsonHelper.GetCommonGson().fromJson(dto.jsonBody, TrackerEvent.class);
                events.add(event);
            }
            return events;
        } catch (Exception e) {
            MLog.e(TAG, e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public int getTrackerEventCounts() {
        int result = 0;
        try {
            QueryBuilder<TrackerDto, Long> builder = this.queryBuilder();
            List<TrackerDto> dtos = builder.query();
            if (dtos != null && !dtos.isEmpty()) {
                result = dtos.size();
            }
        } catch (Exception e) {
            MLog.e(TAG, e.getLocalizedMessage(), e);
        }

        return result;
    }


    @Override
    public void finishUpload(long maxId) {
        DeleteBuilder<TrackerDto, Long> deleteBuilder = this.deleteBuilder();
        try {
            deleteBuilder.where().le(TrackerDto.COL_CTIME, maxId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            MLog.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    //==================构造函数===================
    public TrackerDao(Class<TrackerDto> dataClass) throws SQLException {
        super(dataClass);
    }

    public TrackerDao(ConnectionSource connectionSource, Class<TrackerDto> dataClass)
            throws SQLException {
        super(connectionSource, dataClass);
    }

    public TrackerDao(ConnectionSource connectionSource,
                      DatabaseTableConfig<TrackerDto> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }


}