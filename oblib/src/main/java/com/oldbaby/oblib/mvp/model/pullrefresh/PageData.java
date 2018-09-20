package com.oldbaby.oblib.mvp.model.pullrefresh;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * usage: 列表分页加载通用POJO
 * author: kHRYSTAL
 * create time: 18/9/20
 * update time:
 * email: 723526676@qq.com
 */
public class PageData<T> implements Serializable {
    private static final long serialVersionUID = -5607262592780784855L;

//    // 是否最后一页
//    @SerializedName("lastPage")
//    public boolean pageIsLast = true;

    // 上拉传递给server的页数
    @SerializedName("next")
    public String next;

    // 总数
    @SerializedName("count")
    public long count;

    // 列表数据
    @SerializedName("result")
    public List<T> data;
}
