package com.oldbaby.common.bean;

import com.google.gson.annotations.SerializedName;
import com.oldbaby.oblib.OrmDto;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleTag extends OrmDto {

    @SerializedName("id")
    public long id;

    @SerializedName("tag_name")
    public String tagName;
}
