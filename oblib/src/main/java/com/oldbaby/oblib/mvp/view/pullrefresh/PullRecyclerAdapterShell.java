package com.oldbaby.oblib.mvp.view.pullrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.oldbaby.oblib.mvp.view.IListView;
import com.oldbaby.oblib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * usage: RecyclerView的Adapter
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class PullRecyclerAdapterShell<D extends LogicIdentifiable> extends RecyclerView.Adapter<RecyclerViewHolder> implements IListView<D> {

    public static final int TYPE_HEADER = 5634; //头view 类型
    public static final int TYPE_FOOTER = 5635; //脚view 类型
    public static final int TYPE_EMPTY = 5636;  //数据为空view 类型。包含EmptyView 和 ErrorView

    private LinearLayout headerContainer;  //头view 容器
    private LinearLayout footerContainer;  //脚view 容器
    private LinearLayout emptyContainer;   //数据为空view 容器。包含EmptyView 和 ErrorView

    private View initView;    //尚未加载时展示的view。添加到emptyContainer中通过setVisibility处理是否显示。
    private View emptyView;   //加载完成后，数据为空时的展示view。添加到emptyContainer中通过setVisibility处理是否显示。
    private View errorView;   //数据加载失败时的展示view。添加到emptyContainer中通过setVisibility处理是否显示。

    protected List<D> data = null;

    /**
     * 正常数据view的adapter。提供除去 头 脚 空 view之外的view holder
     */
    private PullRecyclerViewAdapter adapter;

    public PullRecyclerAdapterShell(@NonNull Context context, @NonNull PullRecyclerViewAdapter adapter, PullToRefreshBase.Orientation orientation) {
        this.adapter = adapter;
        adapter.setAdpaterShell(this);
        ViewGroup.LayoutParams lp;
        if (orientation == PullToRefreshBase.Orientation.VERTICAL) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        int linearLayoutOrientation = orientation == PullToRefreshBase.Orientation.VERTICAL ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL;
        headerContainer = new LinearLayout(context);
        headerContainer.setOrientation(linearLayoutOrientation);
        headerContainer.setLayoutParams(lp);
        footerContainer = new LinearLayout(context);
        footerContainer.setOrientation(linearLayoutOrientation);
        footerContainer.setLayoutParams(lp);
        emptyContainer = new LinearLayout(context);
        emptyContainer.setOrientation(linearLayoutOrientation);
        emptyContainer.setLayoutParams(lp);
    }

    //region RecyclerView.Adapter 父类方法的重写
    @Override
    public void onViewRecycled(RecyclerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return data == null ? 3 : data.size() + 3;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            removeParent(headerContainer);
            return new HeaderHolder(headerContainer);
        } else if (viewType == TYPE_FOOTER) {
            removeParent(footerContainer);
            return new FooterHolder(footerContainer);
        } else if (viewType == TYPE_EMPTY) {
            removeParent(emptyContainer);
            return new EmptyHolder(emptyContainer);
        } else {
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (position == 0) {
        } else if (position == 1) {
        } else if (position == getItemCount() - 1) {
        } else {
            adapter.onBindViewHolder(holder, position - 2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == 1) {
            return TYPE_EMPTY;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return adapter.getItemViewType(position - 2);
        }
    }

    private void removeParent(View view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
    //endregion

    //region 对外提供的公共方法

    /**
     * 获取每个item占用的单元数。针对GridLayoutManager
     *
     * @param position item的position
     * @param total    GridLayoutManager的列数
     */
    public int getSpanSize(int position, int total) {
        if (position == 0) {  //头view 占一行
            return total;
        } else if (position == 1) { //空和加载错误的view 占一行
            return total;
        } else if (position == getItemCount() - 1) { //脚view 占一行
            return total;
        } else {
            return adapter.getSpanSize(position - 2, total);
        }
    }

    /**
     * 获取数据的总数。
     */
    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public D getItem(final int position) {
        if (data != null && data.size() > position) {
            return data.get(position);
        }
        return null;
    }

    /**
     * 向指定位置增加数据
     *
     * @param item
     * @param location
     */
    @Override
    public void insert(final D item, int location) {
        if (item == null) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(location, item);
        this.notifyItemInserted(location + 2);
    }

    /**
     * 向指定位置增加一组数据
     *
     * @param items
     * @param location
     */
    @Override
    public void insertItems(final List<? extends D> items, int location) {
        if (items == null || items.size() == 0) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.addAll(location, items);
        this.notifyItemInserted(location + 2);
    }

    /**
     * 清楚所有的数据
     */
    @Override
    public void clearItems() {
        if (this.data != null) {
            this.data.clear();
            this.notifyDataSetChanged();
        }
    }

    /**
     * 删除指定的item
     *
     * @param item
     */
    @Override
    public void removeItem(D item) {
        if (this.data != null) {
            int index = this.data.indexOf(item);
            if (index >= 0) {
                this.data.remove(index);
                this.notifyItemRemoved(index + 2);
            }
        }
    }

    @Override
    public boolean replaceItem(D item) {
        if (item == null || data == null)
            return false;

        String itemLogicIdentity = item.getLogicIdentity();

        int index = -1;
        for (int i = 0, count = getDataCount(); i < count; i++) {
            D src = getItem(i);
            if (src == null) {
                continue;
            }
            String srcLogicIdentity = src.getLogicIdentity();
            if (!StringUtil.isNullOrEmpty(itemLogicIdentity)
                    && !StringUtil.isNullOrEmpty(srcLogicIdentity)
                    && srcLogicIdentity.equals(itemLogicIdentity)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            data.remove(index);
            data.add(index, item);
            notifyItemChanged(index + 2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取所有的数据
     *
     * @return
     */
    @Override
    public List<D> getData() {
        return data;
    }
    //endregion

    //region header footer init empty error view相关的，显示 隐藏 添加等

    /**
     * 添加RecyclerView的头view
     */
    public void addHeader(View v, LinearLayout.LayoutParams lp) {
        headerContainer.addView(v, lp);
    }

    /**
     * 移除RecyclerView的头view
     */
    public void removeHeader() {
        headerContainer.removeAllViews();
    }

    /**
     * 添加RecyclerView的脚view
     */
    public void addFooter(View v, LinearLayout.LayoutParams lp) {
        footerContainer.addView(v, lp);
    }

    /**
     * 移除RecyclerView的脚view
     */
    public void removeFooter() {
        footerContainer.removeAllViews();
    }

    /**
     * 设置尚未加载时展示的view。
     */
    public void setInitView(View v) {
        if (initView != null) {
            emptyContainer.removeView(initView);
        }
        if (v != null) {
            initView = v;
            initView.setVisibility(View.GONE);
            emptyContainer.addView(initView);
        }
    }

    /**
     * 显示尚未加载时展示的view。
     */
    public void showInitView() {
        if (initView != null) {
            initView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏尚未加载时展示的view。
     */
    public void hideInitView() {
        if (initView != null) {
            initView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置加载完成后，数据为空时的展示view。
     */
    public void setEmptyView(View v) {
        if (emptyView != null) {
            emptyContainer.removeView(emptyView);
        }
        if (v != null) {
            emptyView = v;
            emptyView.setVisibility(View.GONE);
            emptyContainer.addView(emptyView);
        }
    }

    /**
     * 获取加载完成后，数据为空时的展示view。
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 显示加载完成后，数据为空时的展示view。
     */
    public void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏加载完成后，数据为空时的展示view。
     */
    public void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置加载失败时的展示view。
     */
    public void setErrorView(View v) {
        if (errorView != null) {
            emptyContainer.removeView(errorView);
        }
        if (v != null) {
            errorView = v;
            errorView.setVisibility(View.GONE);
            emptyContainer.addView(errorView);
        }
    }

    /**
     * 获取加载失败时的展示view。
     */
    public View getErrorView() {
        return errorView;
    }

    /**
     * 显示加载失败时的展示view。
     */
    public void showErrorView() {
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏加载失败时的展示view。
     */
    public void hideErrorView() {
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }

    public static class HeaderHolder extends RecyclerViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void recycle() {
        }
    }

    public static class FooterHolder extends RecyclerViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void recycle() {
        }
    }

    public static class EmptyHolder extends RecyclerViewHolder {

        public EmptyHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void recycle() {
        }
    }
    //endregion
}
