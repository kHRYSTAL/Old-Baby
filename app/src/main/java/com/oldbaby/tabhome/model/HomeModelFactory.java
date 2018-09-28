package com.oldbaby.tabhome.model;

import com.oldbaby.tabhome.model.impl.TabHomeModel;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class HomeModelFactory {

    public static ITabHomeModel CreateTabHomeModel() {
        return new TabHomeModel();
    }
}
