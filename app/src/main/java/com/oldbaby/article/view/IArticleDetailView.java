package com.oldbaby.article.view;

import android.content.Context;

import com.oldbaby.common.bean.PageItem;
import com.oldbaby.oblib.mvp.view.IMvpView;

import java.util.HashMap;
import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public interface IArticleDetailView extends IMvpView {
    // 显示错误布局
    void showArticleErrorView();
    // 隐藏错误布局
    void hideArticleErrorView();
    void showArticleEmptyView();
    void hideArticleEmptyView();
    // 点击语音合成
    void onSpeechSynthesizerClick();
    // 获取文章全部文字列表
    List<String> getArticleDetailTexts();

    Context getViewContext();

    void setDataToView(List<PageItem> items);

    void setPlayButtonType(int type);
    void hidePlayer();
    void showPlayer();

    void startSpeak(int pos);

    void endSpeak(int pos);

    void setReferer(String referer);

    void watchImage(int imagePosition, HashMap<String, String> header);

    void setArticleHeader(String title, String source, String pbTime);

    void setCover(String refer, String url);

}
