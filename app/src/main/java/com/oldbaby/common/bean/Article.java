package com.oldbaby.common.bean;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.oldbaby.oblib.OrmDto;
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
public class Article extends OrmDto {

    @SerializedName("content")
    public String content;

    public List<PageItem> getArticle() {
        if (StringUtil.isNullOrEmpty(content))
            return null;
        else
            return GsonHelper.GetCommonGson().fromJson(content, new TypeToken<List<PageItem>>() {
            }.getType());
    }
}
