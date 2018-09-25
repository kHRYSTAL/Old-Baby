package com.oldbaby.common.dto;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.oldbaby.oblib.OrmDto;

import java.io.Serializable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/25
 * update time:
 * email: 723526676@qq.com
 */
@DatabaseTable(tableName = CacheDto.TB_NAME, daoClass = CacheDao.class)
public class CacheDto extends OrmDto {

    private static final long serialVersionUID = 1L;

    public static final String TB_NAME = "cache_dto";
    public static final String COL_KEY = "key";
    public static final String COL_VALUE = "value";

    @DatabaseField(columnName = COL_KEY, id = true)
    public String key;

    @DatabaseField(columnName = COL_VALUE, dataType = DataType.SERIALIZABLE)
    public Serializable value;

}
