package com.oldbaby.common.bean;

import com.oldbaby.oblib.OrmDto;
import com.oldbaby.oblib.mvp.view.pullrefresh.LogicIdentifiable;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/27
 * update time:
 * email: 723526676@qq.com
 */
public class Feed extends OrmDto implements LogicIdentifiable {
    public String thumb;
    public String title;
    public long id;
    public String url;

    @Override
    public String getLogicIdentity() {
        return String.valueOf(id);
    }
}
