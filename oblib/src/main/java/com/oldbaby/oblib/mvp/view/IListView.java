package com.oldbaby.oblib.mvp.view;

import java.util.List;

/**
 * usage: 列表页面的通用接口
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public interface IListView<D> {
    /**
     * 当前视图中的数据总数
     * @return
     */
    int getItemCount();

    /**
     * 获取当前视图中指定位置的某条数据
     * @param position
     * @return
     */
    D getItem(int position);

    /**
     * 插入数据到当前视图中的指定位置
     *
     * @param item
     * @param location
     */
    void insert(D item, int location);

    /**
     * 插入一组数据到当前视图中的指定位置
     *
     * @param items
     * @param location
     */
    void insertItems(List<? extends D> items, int location);

    /**
     * 清除当前视图中的所有数据
     */
    void clearItems();

    /**
     * 从当前视图删除指定数据
     *
     * @param item
     */
    void removeItem(D item);

    /**
     * 根据item ID替换指定的数据
     *
     * @param item
     * @return 存在相同ID的数据
     */
    boolean replaceItem(D item);

    List<D> getData();
}
