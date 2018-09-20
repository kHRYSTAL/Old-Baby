package com.oldbaby.oblib.mvp.view.pullrefresh;

import com.oldbaby.oblib.mvp.model.pullrefresh.PageData;
import com.oldbaby.oblib.mvp.view.IMvpView;

import java.util.List;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IPullView<D extends LogicIdentifiable> extends IMvpView {

    /**
     * 执行下拉刷新
     */
    void pullDownToRefresh(boolean showRefreshingHeader);

    /**
     * 执行下拉刷新
     */
    void pullUpToLoadMore(boolean showRefreshing);

    /**
     * 结束刷新
     */
    void onRefreshFinished();

    /**
     * 加载数据成功
     */
    void onLoadSucessfully(List<D> items);

    /**
     * 加载数据成功
     */
    void onLoadSucessfully(PageData<D> dataList);

    /**
     * 加载数据失败
     */
    void onLoadFailed(Throwable failture);

    /**
     * 清除数据
     */
    void cleanData();

    /**
     * 在数据最后追加数据
     */
    void appendItems(List<D> items);

    /**
     * 根据error显示空view，并隐藏initView。
     * 数据加载之后调用该方法。
     * 如果当前列表数据不为空，空view和错误view都不显示
     */
    void showEmptyView();

    /**
     * 根据error显示错误view，并隐藏initView。
     * 数据加载之后调用该方法。
     * 如果当前列表数据不为空，空view和错误view都不显示
     */
    void showErrorView();

    /**
     * 获取当前的数据数组
     */
    List<D> getData();

    /**
     * 获取当前的数据数组中的一项
     */
    D getItem(int position);

    /**
     * 刷新列表
     */
    void refresh();

    /**
     * 移除其中一项
     */
    void remove(D item);

    /**
     * 在location位置插入一项
     */
    void insert(D item, int location);

    /**
     * 在location位置插入一组数据
     */
    void insertItems(List<D> items, int location);

    /**
     * 根据逻辑身份移除某一项
     *
     * @param logicId 逻辑ID
     */
    void logicIdDelete(String logicId);

    /**
     * 根据逻辑身份替换某一项
     *
     * @param item 要替换的数据
     */
    void logicIdReplace(D item);

    int getDataCount();
}
