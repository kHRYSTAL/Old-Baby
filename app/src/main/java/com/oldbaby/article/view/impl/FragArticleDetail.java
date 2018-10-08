package com.oldbaby.article.view.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldbaby.R;
import com.oldbaby.article.model.impl.ArticleDetailModel;
import com.oldbaby.article.presenter.ArticleDetailPresenter;
import com.oldbaby.article.view.IArticleDetailView;
import com.oldbaby.common.base.CommonFragActivity;
import com.oldbaby.common.base.TitleBarProxy;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.common.view.zoompage.PinchZoomPage;
import com.oldbaby.oblib.mvp.presenter.BasePresenter;
import com.oldbaby.oblib.mvp.view.FragBaseMvps;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.EmptyView;
import com.oldbaby.oblib.view.NetErrorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * usage: 文章详情页
 * author: kHRYSTAL
 * create time: 18/9/30
 * update time:
 * email: 723526676@qq.com
 */
public class FragArticleDetail extends FragBaseMvps implements IArticleDetailView {

    private static final String TAG = FragArticleDetail.class.getSimpleName();
    private static final String PAGE_NAME = "ArticleDetail";

    private static final String INK_ARTICLE_ID = "ink_article_id";

    private static final int TAG_ID_PLAY = 22;

    @BindView(R.id.zoomPage)
    PinchZoomPage zoomPage;
    @BindView(R.id.errorView)
    NetErrorView errorView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;

    private ArticleDetailPresenter presenter;
    private TextView tbPlay;

    public static void invoke(Context context, String articleId) {
        if (articleId == null)
            return;
        CommonFragActivity.CommonFragParams param = new CommonFragActivity.CommonFragParams();
        param.clsFrag = FragArticleDetail.class;
        param.title = "文章详情";
        param.enableBack = true;
        param.runnable = titleRunnable;
        param.titleBtns = new ArrayList<CommonFragActivity.TitleBtn>();
        CommonFragActivity.TitleBtn tb = new CommonFragActivity.TitleBtn(TAG_ID_PLAY, CommonFragActivity.TitleBtn.TYPE_TXT);
        tb.btnText = "开始";
        param.titleBtns.add(tb);
        Intent intent = CommonFragActivity.createIntent(context, param);
        intent.putExtra(INK_ARTICLE_ID, articleId);
        context.startActivity(intent);
    }

    static CommonFragActivity.TitleRunnable titleRunnable = new CommonFragActivity.TitleRunnable() {

        @Override
        protected void execute(Context context, Fragment fragment) {
            // do nothing
        }
    };

    @Override
    protected Map<String, BasePresenter> createPresenters() {
        Map<String, BasePresenter> map = new HashMap<>();
        presenter = new ArticleDetailPresenter();
        presenter.setArticleId(getActivity().getIntent().getStringExtra(INK_ARTICLE_ID));

        presenter.setModel(new ArticleDetailModel());
        map.put(ArticleDetailPresenter.class.getSimpleName(), presenter);
        return map;
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_article_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    private void initViews(View rootView) {
        // 获取title bar上的按钮 文字默认为开始 点击时变为暂停
        TitleBarProxy titleBar = ((CommonFragActivity) getActivity()).getTitleBar();
        tbPlay = (TextView) titleBar.getButton(TAG_ID_PLAY);
        tbPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSpeechSynthesizerClick();
            }
        });
        errorView.setBtnReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getArticleDetailFromInternet();
            }
        });
        emptyView.setImgRes(com.oldbaby.oblib.R.drawable.img_load_empty_def);
        emptyView.setPrompt("暂无内容");
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        zoomPage.setOnPageItemClickListener(new PinchZoomPage.OnPageItemClickListener() {
            @Override
            public void onTextClick(TextView textView, int textPosition) {

            }

            @Override
            public void onImageClick(ImageView imageView, int imagePosition, List<String> allImageUrls) {

            }

            @Override
            public void onViewClick(View view) {

            }
        });
    }

    @Override
    public void showArticleErrorView() {
        errorView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        zoomPage.setVisibility(View.GONE);
    }

    @Override
    public void hideArticleErrorView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        zoomPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showArticleEmptyView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        zoomPage.setVisibility(View.GONE);
    }

    @Override
    public void hideArticleEmptyView() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        zoomPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSpeechSynthesizerClick() {
        presenter.onSpeechSynthesizerClick();
    }

    @Override
    public List<String> getArticleDetailTexts() {
        List<String> textsFromItems = null;
        if (zoomPage == null)
            return textsFromItems;
        textsFromItems = zoomPage.getTextsFromItems();
        return textsFromItems;
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    @Override
    public void setDataToView(List<PageItem> items) {
        zoomPage.setContent(items);
        zoomPage.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPlayButtonText(String text) {
        if (tbPlay != null) {
            tbPlay.setVisibility(View.VISIBLE);
            tbPlay.setText(text);
        }
    }

    @Override
    public void hidePlayButtonText() {
        if (tbPlay != null) {
            tbPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void startSpeak(int pos) {
        if (zoomPage.getTextViewItems() != null && !zoomPage.getTextViewItems().isEmpty()) {
            TextView textView = zoomPage.getTextViewItems().get(pos);
            textView.setTextColor(Color.RED);
            int location = textView.getTop();
//            int aboveDistance = 0;
//            int curPosInPage = (int) textView.getTag(R.id.item_view);
            // 获取改控件上方所有控件高度与间距总和
//            if (zoomPage.getAllViews() != null && !zoomPage.getAllViews().isEmpty()) {
//                for (int i = curPosInPage; i >= 0; i--) {
//                    aboveDistance += zoomPage.getAllViews().get(i).getHeight();
//                    // 目前没有间距 如果包含间距也需要计算
//                }
//                aboveDistance -= view.getHeight();
//            }

            // when extend NestedScrollView need add this method, else smoothScrollTo will has default fling distance
            zoomPage.fling(0); 
            zoomPage.smoothScrollTo(0, location);
        }
    }

    @Override
    public void endSpeak(int pos) {
        if (zoomPage.getTextViewItems() != null && !zoomPage.getTextViewItems().isEmpty()) {
            TextView textView = zoomPage.getTextViewItems().get(pos);
            textView.setTextColor(Color.GRAY);
        }
    }

    @Override
    public void setReferer(String referer) {
        zoomPage.setImageReferer(referer);
    }
}
