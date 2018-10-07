package com.oldbaby.feed.view;

import com.oldbaby.common.bean.Article;
import com.oldbaby.oblib.mvp.view.pullrefresh.IPullView;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public interface IFeedTabView extends IPullView<Article> {

    // 跳转至文章详情
    void jumpArticleDetail(String articleId);
}
