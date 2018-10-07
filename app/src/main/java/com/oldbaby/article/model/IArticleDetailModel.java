package com.oldbaby.article.model;


import com.oldbaby.common.bean.Article;
import com.oldbaby.oblib.mvp.model.IMvpModel;

import rx.Observable;

/**
 * usage: 文章详情model
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public interface IArticleDetailModel extends IMvpModel {

    // 获取文章详情
    Observable<Article> getArticleDetail(String articleId);
}
