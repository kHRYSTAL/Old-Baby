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

    public static final int TYPE_ARTICLE_COMMON = 11;
    public static final int TYPE_VIDEO_COMMON = 31;

    @SerializedName("id")
    public String id;

    @SerializedName("type")
    public int type;

    @SerializedName("title")
    public String title;

    @SerializedName("keywords")
    public String keywords;

    // 文章副标题
    @SerializedName("sub_title")
    public String subTitle;

    // 视频链接
    @SerializedName("video_url")
    public String videoUrl;

    // 视频时长
    @SerializedName("duration")
    public String duration;

    @SerializedName("thumb_pic_url")
    public String thumbPicUrl;

    @SerializedName("insert_method")
    public Integer insertMethod;

    @SerializedName("source")
    public String source;

    @SerializedName("stick")
    public boolean stick;

    @SerializedName("hot")
    public boolean hot;

    @SerializedName("spider_url")
    public String spiderUrl;

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

    // 文章详情
    @SerializedName("content")
    public String content;

    @SerializedName("tag")
    public ArticleTag tag;

    // 获取文章内容
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
