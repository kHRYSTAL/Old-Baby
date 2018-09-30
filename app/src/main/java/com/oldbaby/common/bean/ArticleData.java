package com.oldbaby.common.bean;

import com.google.gson.annotations.SerializedName;
import com.oldbaby.oblib.OrmDto;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/30
 * update time:
 * email: 723526676@qq.com
 */
public class ArticleData extends OrmDto {

    @SerializedName("article")
    public Article article;

    public List<PageItem> getArticle() {
        if (article == null)
            return null;
        else
            return article.getArticle();
    }
}
