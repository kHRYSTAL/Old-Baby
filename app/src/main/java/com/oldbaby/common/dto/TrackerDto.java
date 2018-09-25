package com.oldbaby.common.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.oldbaby.oblib.OrmDto;
import com.oldbaby.oblib.util.gson.GsonHelper;
import com.oldbaby.tracker.bean.TrackerEvent;

@DatabaseTable(tableName = TrackerDto.TB_NAME, daoClass = TrackerDao.class)
public class TrackerDto extends OrmDto {

    public static final String TB_NAME = "tb_tracker";

    public TrackerDto() {
    }

    public TrackerDto(TrackerEvent event) {
        this.sessionId = event.sessionId;
        this.ctime = event.time;
        this.jsonBody = GsonHelper.GetCommonGson().toJson(event);
    }

    public static final String COL_SESSION = "session_id";
    public static final String COL_CTIME = "ctime";
    public static final String COL_BODY = "json_body";

    @DatabaseField(columnName = COL_CTIME, id = true)
    public long ctime;

    @DatabaseField(columnName = COL_SESSION)
    public String sessionId;

    @DatabaseField(columnName = COL_BODY)
    public String jsonBody;
}