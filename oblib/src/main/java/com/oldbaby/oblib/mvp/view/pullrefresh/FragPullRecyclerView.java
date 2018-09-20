package com.oldbaby.oblib.mvp.view.pullrefresh;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.oldbaby.oblib.R;
import com.oldbaby.oblib.mvp.presenter.pullrefresh.BasePullPresenter;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.view.EmptyView;
import com.oldbaby.oblib.view.NetErrorView;
import com.oldbaby.oblib.view.pulltorefresh.PullEvent;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public abstract class FragPullRecyclerView<D extends LogicIdentifiable, P extends BasePullPresenter> extends FragBasePullMvps<RecyclerView, D, P> {

    public static final String TAG = "FragPullRecycleView";

    private PullRecyclerAdapterShell<D> adapter; //RecycleView的adapter

    private int scrollToShowMore = DensityUtil.dip2px(35);

    //region 生命周期 及父类方法重写
    @Override
    protected View createDefaultFragView() {
        return LayoutInflater.from(getActivity()).inflate(getOrientation() == PullToRefreshBase.Orientation.VERTICAL ?
                R.layout.pull_to_refresh_recycle : R.layout.pull_to_refresh_recycle_h, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        pullView.setBackgroundColor(getResources().getColor(R.color.color_bg2));
        internalView.setBackgroundColor(getResources().getColor(R.color.color_bg2));
        internalView.setAdapter(adapter);
        final RecyclerView.LayoutManager lm = makeLayoutManager();
        if (lm instanceof GridLayoutManager) {
            ((GridLayoutManager) lm).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return adapter.getSpanSize(position, ((GridLayoutManager) lm).getSpanCount());
                }
            });
        }
        internalView.setLayoutManager(lm);
        internalView.addOnScrollListener(scrollListener);
        return v;
    }


    private void initAdapter() {
        adapter = new PullRecyclerAdapterShell(getActivity(), makeAdapter(), getOrientation());
        adapter.setInitView(makeInitView(getActivity()));
        adapter.setEmptyView(makeEmptyView(getActivity()));
        adapter.setErrorView(makeErrorView(getActivity()));
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        int scrollState;

        @Override
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            this.scrollState = scrollState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // 如果滚动的位置已经超过倒数第二条，并且不是最后一页，以及没有正在刷新，则自动加载后面更多
            if (isLastPage || scrollState == 0)
                return;
            if (isRefreshing())
                return;
            View lastVisibleChild = internalView.getChildAt(internalView.getChildCount() - 1);
            int lastVisiblePosition = lastVisibleChild != null ? internalView.getChildAdapterPosition(lastVisibleChild) : -1;
            if (lastVisiblePosition >= internalView.getAdapter().getItemCount() - 3) {
                currentEvent = PullEvent.more;
                basePullPresenter.loadMore(next);
            }
        }
    };

    @Override
    protected void scrollToShowLoadMoreData() {
        if (getOrientation() == PullToRefreshBase.Orientation.VERTICAL) {
            internalView.smoothScrollBy(0, scrollToShowMore);
        } else {
            internalView.smoothScrollBy(scrollToShowMore, 0);
        }
    }

    //endregion

    //region IPullView 方法实现
    @Override
    public List<D> getData() {
        return adapter.getData();
    }

    @Override
    public D getItem(int position) {
        return adapter.getItem(position);
    }

    @Override
    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    public void refreshItem(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void insert(D item, int location) {
        adapter.insert(item, location);
    }

    @Override
    public void insertItems(final List<D> items, int location) {
        adapter.insertItems(items, location);
    }

    @Override
    public void appendItems(List<D> items) {
        adapter.insertItems(items, adapter.getDataCount());
    }

    @Override
    public void remove(D item) {
        adapter.removeItem(item);
        onDataReduce();
    }

    @Override
    public void cleanData() {
        adapter.clearItems();
        onDataReduce();
    }

    @Override
    public void logicIdDelete(String logicId) {
        List<D> data = getData();
        if (data != null && !StringUtil.isNullOrEmpty(logicId)) {
            for (int i = data.size() - 1; i >= 0; i--) {
                if (data.get(i) != null && data.get(i).getLogicIdentity() != null && data.get(i).getLogicIdentity().equals(logicId)) {
                    remove(data.get(i));
                    return;
                }
            }
        }
    }

    @Override
    public void logicIdReplace(D item) {
        adapter.replaceItem(item);
    }

    @Override
    public void showEmptyView() {
        adapter.hideInitView();
        if (adapter.getDataCount() == 0) {
            adapter.showEmptyView();
            adapter.hideErrorView();
        } else {
            adapter.hideErrorView();
            adapter.hideEmptyView();
        }
    }

    @Override
    public void showErrorView() {
        adapter.hideInitView();
        if (adapter.getDataCount() == 0) {
            adapter.showErrorView();
            adapter.hideEmptyView();
        } else {
            adapter.hideErrorView();
            adapter.hideEmptyView();
        }
    }

    //endregion

    //region Header、Footer、Empty、Error View的添加删除等
    protected void addHeader(View v, LinearLayout.LayoutParams lp) {
        adapter.addHeader(v, lp);
    }

    protected void removeHeader() {
        adapter.removeHeader();
    }

    protected void addFooter(View v, LinearLayout.LayoutParams lp) {
        adapter.addFooter(v, lp);
    }

    protected void removeFooter() {
        adapter.removeFooter();
    }

    protected void setEmptyView(View v) {
        adapter.setEmptyView(v);
    }

    protected void setErrorView(View v) {
        adapter.setErrorView(v);
    }

    /**
     * 生成默认的 未加载时的view
     */
    protected View makeInitView(Context context) {
        EmptyView emptyView = new EmptyView(context);
        emptyView.setImgRes(R.drawable.img_load_empty_def);
        emptyView.setPrompt("暂无内容");
        emptyView.setBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInitBtnClick();
            }
        });
        emptyView.setVisibility(View.GONE);
        emptyView.setPadding(0, DensityUtil.dip2px(150), 0, DensityUtil.dip2px(150));
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return emptyView;
    }

    /**
     * 生成默认的 数据为空时的view
     */
    protected View makeEmptyView(Context context) {
        EmptyView emptyView = new EmptyView(context);
        emptyView.setImgRes(R.drawable.img_load_empty_def);
        emptyView.setPrompt("暂无内容");
        emptyView.setBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyBtnClick();
            }
        });
        emptyView.setVisibility(View.GONE);
        emptyView.setPadding(0, DensityUtil.dip2px(150), 0, DensityUtil.dip2px(150));
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return emptyView;
    }

    /**
     * 生成默认的 数据加载失败时的view
     */
    protected View makeErrorView(Context context) {
        NetErrorView errorView = new NetErrorView(context);
        if (showSmallErrorViewIcon()) {
            errorView.setImgRes(R.drawable.img_empty_nowifi_small);
        }
        errorView.setBtnReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorBtnClick();
            }
        });
        errorView.setVisibility(View.GONE);
        errorView.setPadding(0, DensityUtil.dip2px(150), 0, DensityUtil.dip2px(150));
        errorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return errorView;
    }

    /**
     * 在半截列表中显示小图标，默认显示大图标
     */
    protected boolean showSmallErrorViewIcon() {
        return false;
    }

    protected View getEmptyView() {
        if (adapter != null) {
            return adapter.getEmptyView();
        }
        return null;
    }

    protected View getErrorView() {
        if (adapter != null) {
            return adapter.getErrorView();
        }
        return null;
    }

    protected void onInitBtnClick() {

    }

    protected void onEmptyBtnClick() {

    }

    protected void onErrorBtnClick() {
        pullDownToRefresh(true);
    }
    //endregion

    protected abstract PullRecyclerViewAdapter makeAdapter();

    protected RecyclerView.LayoutManager makeLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
    }

    protected PullToRefreshBase.Orientation getOrientation() {
        return PullToRefreshBase.Orientation.VERTICAL;
    }

    /**
     * 当前adapter中的数据被移除或清空时调用。
     * 如果当前不是正在刷新，且当前数据为空：判断当前是否为最后一页，如果是，显示空view，如果不是自动上拉加载更多。
     */
    public void onDataReduce() {
        if (isRefreshing()) {
            return;
        }
        if (adapter.getDataCount() == 0) {
            pullDownToRefresh(true);
        }
    }

    /**
     * 获取数据总数
     */
    @Override
    public int getDataCount() {
        return adapter.getDataCount();
    }
}
