package com.oldbaby.feed.view.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.oldbaby.R;
import com.oldbaby.article.view.impl.FragArticleDetail;
import com.oldbaby.common.bean.Article;
import com.oldbaby.common.util.SpiderHeader;
import com.oldbaby.common.view.roundedimageview.RoundedImageView;
import com.oldbaby.feed.model.impl.FeedTabModel;
import com.oldbaby.feed.presenter.FeedTabPresenter;
import com.oldbaby.feed.view.IFeedTabView;
import com.oldbaby.oblib.mvp.view.pullrefresh.FragPullRecyclerView;
import com.oldbaby.oblib.mvp.view.pullrefresh.PullRecyclerViewAdapter;
import com.oldbaby.oblib.mvp.view.pullrefresh.RecyclerViewHolder;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/7
 * update time:
 * email: 723526676@qq.com
 */
public class FragFeedTab extends FragPullRecyclerView<Article, FeedTabPresenter> implements IFeedTabView {

    private static final String TAG = FragFeedTab.class.getSimpleName();
    private static final String PAGE_NAME = FragFeedTab.class.getSimpleName();

    private FeedTabPresenter presenter;

    @Override
    protected FeedTabPresenter makePullPresenter() {
        presenter = new FeedTabPresenter();
        presenter.setModel(new FeedTabModel());
        return presenter;
    }

    @Override
    protected PullRecyclerViewAdapter makeAdapter() {
        PullRecyclerViewAdapter<ItemHolder> adapter = new PullRecyclerViewAdapter<ItemHolder>() {
            @Override
            public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_article, parent, false);
                return new ItemHolder(view);
            }

            @Override
            public void onBindViewHolder(ItemHolder holder, int position) {
                holder.fill(getItem(position));
            }
        };
        return adapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pullView.setBackgroundColor(getResources().getColor(R.color.transparent));
        internalView.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater
                .inflate(R.layout.frag_tab_item, container, false);
        ((LinearLayout) rootView.findViewById(R.id.llContainer)).addView(super.onCreateView(inflater, container, savedInstanceState),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        internalView.setPadding(0, DensityUtil.dip2px(120), 0, 0);
        internalView.setClipChildren(false);
        internalView.setClipToPadding(false);
    }

    @Override
    public void jumpArticleDetail(String articleId) {
        FragArticleDetail.invoke(getActivity(), articleId);
    }

    @Override
    public String getPageName() {
        return PAGE_NAME;
    }

    class ItemHolder extends RecyclerViewHolder {


        @BindView(R.id.ivInfoImg)
        RoundedImageView ivInfoImg;
        @BindView(R.id.tvInfoTitle)
        TextView tvInfoTitle;
        @BindView(R.id.tvInfoSource)
        TextView tvInfoSource;
        @BindView(R.id.tvInfoTime)
        TextView tvInfoTime;
        @BindView(R.id.itemView)
        RelativeLayout itemView;

        private Article article;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void fill(Article article) {
            this.article = article;
            if (article != null) {
                if (!StringUtil.isNullOrEmpty(article.thumbPicUrl) && getActivity() != null) {
                    ivInfoImg.setVisibility(View.VISIBLE);
                    Headers headers = SpiderHeader.getInstance()
                            .addRefer(article.spiderUrl)
                            .build();
                    Glide.with(getActivity()).load(new GlideUrl(article.thumbPicUrl, headers)).into(ivInfoImg);
                } else {
                    ivInfoImg.setVisibility(View.GONE);
                }
                if (!StringUtil.isNullOrEmpty(article.title)) {
                    tvInfoTitle.setVisibility(View.VISIBLE);
                    tvInfoTitle.setText(article.title);
                } else {
                    tvInfoTitle.setVisibility(View.GONE);
                }
                if (!StringUtil.isNullOrEmpty(article.source)) {
                    tvInfoSource.setVisibility(View.VISIBLE);
                    tvInfoSource.setText(article.source);
                } else {
                    tvInfoSource.setVisibility(View.GONE);
                }
                if (!StringUtil.isNullOrEmpty(article.pbTime)) {
                    tvInfoTime.setVisibility(View.VISIBLE);
                    tvInfoTime.setText(article.pbTime);
                } else {
                    tvInfoTime.setVisibility(View.GONE);
                }
            }
        }

        @OnClick({R.id.itemView})
        public void onClickItem() {
            presenter.onClickArticleItem(article.id);
        }

        @Override
        public void recycle() {

        }
    }
}
