package com.oldbaby.feed.view.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oldbaby.R;
import com.oldbaby.article.view.impl.FragArticleDetail;
import com.oldbaby.common.bean.Article;
import com.oldbaby.feed.model.impl.FeedTabModel;
import com.oldbaby.feed.presenter.FeedTabPresenter;
import com.oldbaby.feed.view.IFeedTabView;
import com.oldbaby.oblib.mvp.view.pullrefresh.FragPullRecyclerView;
import com.oldbaby.oblib.mvp.view.pullrefresh.PullRecyclerViewAdapter;
import com.oldbaby.oblib.mvp.view.pullrefresh.RecyclerViewHolder;
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
        pullView.setBackgroundColor(getResources().getColor(R.color.white));
        internalView.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater
                .inflate(R.layout.frag_tab_item, container, false);
        rootView.addView(super.onCreateView(inflater, container, savedInstanceState),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // TODO: 18/10/7 增加搜索栏 holder
        ButterKnife.bind(this, rootView);
        return rootView;
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
        ImageView ivInfoImg;
        @BindView(R.id.tvFrom)
        TextView tvFrom;
        @BindView(R.id.tvInfoTitle)
        TextView tvInfoTitle;
        @BindView(R.id.itemView)
        RelativeLayout itemView;
        @BindView(R.id.tvRead)
        TextView tvRead;
        @BindView(R.id.tvComment)
        TextView tvComment;

        private Article article;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void fill(Article article) {
            this.article = article;
            if (article != null) {
                if (!StringUtil.isNullOrEmpty(article.thumbPicUrl))
//                    Glide.with(getContext()).load(article.thumbPicUrl).into(ivInfoImg);
                if (!StringUtil.isNullOrEmpty(article.title))
                    tvInfoTitle.setText(article.title);
                if (!StringUtil.isNullOrEmpty(article.source))
                    tvFrom.setText(article.source);
                tvRead.setText(article.getReadNums());
                tvComment.setText(article.getCommentNums());
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
