package com.oldbaby.common.bean;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.oldbaby.oblib.OrmDto;
import com.oldbaby.oblib.mvp.view.pullrefresh.LogicIdentifiable;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.gson.GsonHelper;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/30
 * update time:
 * email: 723526676@qq.com
 */
public class Article extends OrmDto implements LogicIdentifiable {

    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("keywords")
    public String keywords;

    @SerializedName("sub_title")
    public String subTitle;

    @SerializedName("thumb_pic_url")
    public String thumbPicUrl;

    @SerializedName("insert_method")
    public Integer insertMethod;

    @SerializedName("duration")
    public Long duration;

    @SerializedName("source")
    public String source;

    @SerializedName("stick")
    public boolean stick;

    @SerializedName("hot")
    public boolean hot;

    @SerializedName("comment_nums")
    public int commentNums;

    @SerializedName("praise_nums")
    public int praiseNums;

    @SerializedName("browse_nums")
    public int browseNums;

    @SerializedName("pb_time")
    public String pbTime;

    @SerializedName("insert_time")
    public String insertTime;

    @SerializedName("update_time")
    public String updateTime;

    @SerializedName("content")
    public String content;

    public List<PageItem> getArticle() {
        if (StringUtil.isNullOrEmpty(content))
            return null;
        else
            return GsonHelper.GetCommonGson().fromJson(content, new TypeToken<List<PageItem>>() {
            }.getType());
    }

    public String getReadNums() {
        return String.valueOf(browseNums);
    }

    public String getCommentNums() {
        return String.valueOf(commentNums);
    }

    @Override
    public String getLogicIdentity() {
        return id;
    }
}
