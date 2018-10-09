package com.oldbaby.oblib.component.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.mvp.view.IListView;
import com.oldbaby.oblib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 如果需要使用listview本身的RecyclerListener，需要显示调用{@link #listView
 * #setAbsView(AbsListView)};否则recycle方法不会执行，容易引起内存溢出
 * <p>
 * XXAdapter adapter= new XXAdapter();
 * <p>
 * adapter.setAbsView(listView);
 *
 * @param <T>: List item's data entity
 * @author Xiangfeiy
 */
public abstract class BaseListAdapter<T> extends BaseAdapter implements IListView<T> {
    protected List<T> data = null;
    protected LayoutInflater inflater = null;
    protected AbsListView listView = null;
    protected String gaString;
    protected OnAdapterChangeListener changedListener;

    protected int firstVisiblePos = -1;
    protected int lastVisiblePos = -1;
    protected int scrollState;

    protected AbsListView.RecyclerListener recycleListener = new CatchableRecyclerListener() {
        @Override
        public void intlOnMovedToScrapHeap(final View view) {
            if (view != null) {
                view.destroyDrawingCache();
                BaseListAdapter.this.recycleView(view);
            }
        }
    };

    protected ViewGroup.OnHierarchyChangeListener childViewRemovedListener = new CatchableOnHierarchyChangeListener() {

        @Override
        public void intlOnChildViewAdded(final View parent, final View child) {
            return;
        }

        @Override
        public void intlOnChildViewRemoved(final View parent, final View child) {
            if (child != null) {
                BaseListAdapter.this.recycleView(child);
            }
        }
    };

    public BaseListAdapter() {
        this(null);
    }

    /**
     *
     */
    public BaseListAdapter(final List<T> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(OGApplication.APP_CONTEXT);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (changedListener != null) {
            int count = data == null ? 0 : data.size();
            changedListener.onDataChanged(count);
        }
    }

    /**
     * 设置关联的absview，处理recycle事件
     *
     * @param listView
     */
    public void setAbsView(final AbsListView listView) {
        this.listView = listView;
        this.listView.setRecyclerListener(this.recycleListener);
        this.listView
                .setOnHierarchyChangeListener(this.childViewRemovedListener);

    }

    public void setDataChangedListener(OnAdapterChangeListener listener) {
        this.changedListener = listener;
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        int count = 0;
        if (this.data != null) {
            count = this.data.size();
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(final int position) {

        T item = null;
        if (this.data != null) {
            item = this.data.get(position);
        }
        return item;
    }

    /**
     * if -1 returned, indicate no item in adapter find
     */
    public int getItemPosition(T item) {
        if (data == null)
            return -1;

        return data.indexOf(item);
    }

    public void add(final List<T> items) {

        if (items == null) {
            this.notifyDataSetChanged();
            return;
        }

        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.addAll(items);

        this.notifyDataSetChanged();
    }

    public void add(int location, final List<T> items) {

        if (items == null) {
            this.notifyDataSetChanged();
            return;
        }

        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.addAll(location, items);

        this.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public void tryAdd(List<?> items) {
        if (items == null) {
            return;
        }

        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        for (Object obj : items) {
            data.add((T) obj);
        }

    }

    public void add(final T item) {

        if (item == null) {
            return;
        }

        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.add(item);
        this.notifyDataSetChanged();
    }

    public void insert(final List<T> items) {

        if (items == null) {
            return;
        }

        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.addAll(0, items);

        this.notifyDataSetChanged();
    }

    public void insert(final T item) {
        if (item == null) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.add(0, item);
        this.notifyDataSetChanged();
    }

    public void insert(final T item, int pos) {
        if (item == null) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.add(pos, item);
        this.notifyDataSetChanged();
    }

    @Override
    public void insertItems(List<? extends T> items, int location) {
        if (items == null) {
            return;
        }
        if (this.data == null) {
            this.data = new ArrayList<T>();
        }
        this.data.addAll(location, items);
        this.notifyDataSetChanged();
    }

    public void clearItems() {
        if (this.data != null) {
            this.data.clear();
            this.notifyDataSetChanged();
        }
    }

    public void removeItems(ArrayList<T> items) {
        if (this.data != null) {
            for (T item : items) {
                this.data.remove(item);
            }
            this.notifyDataSetChanged();
        }
    }

    public void removeItem(T item) {
        if (this.data != null) {
            this.data.remove(item);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public boolean replaceItem(T item) {
        return false;
    }

    public void replaceItem(int index, T item) {
        if (data == null) {
            data = new ArrayList<T>();
            data.add(item);
            return;
        }
        if (data.size() > index) {
            data.remove(index);
            data.add(index, item);
        } else {
            data.add(item);
        }
    }

    @SuppressWarnings("unchecked")
    public void tryRemove(Object item) {
        try {
            removeItem((T) item);
        } catch (Exception ex) {
            removeItems((ArrayList<T>) item);
        }
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(final int position) {
        return 0;
    }

    @Override
    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public void recycleAllView() {
        int viewCount = this.listView.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            View view = this.listView.getChildAt(i);
            if (view != null) {
                view.destroyDrawingCache();
                this.recycleView(view);
            }
        }
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    public void setGaString(String gaString) {
        this.gaString = gaString;
    }

    protected void loadImg(String imgUrl, ImageView view, int plhId) {
        if (StringUtil.isNullOrEmpty(imgUrl)) {
            view.setImageResource(plhId);
            return;
        }
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            Glide.with(view.getContext())
                    .load(imgUrl)
                    .into(view);
        } else {
            view.setImageResource(plhId);
        }
    }

    protected void loadImg(String imgUrl, LazyHeaders headers, ImageView view, int plhId) {
        if (StringUtil.isNullOrEmpty(imgUrl)) {
            view.setImageResource(plhId);
            return;
        }
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            Glide.with(view.getContext())
                    .load(new GlideUrl(imgUrl, headers))
                    .into(view);
        } else {
            view.setImageResource(plhId);
        }
    }

    public boolean shouldLoadImg(String imgUrl) {
        return (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING) ? true : false;
    }

    /**
     * listview是否在滚动状态
     */
    public boolean isScroll() {
        return scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING;
    }

    protected abstract void recycleView(final View view);

}
