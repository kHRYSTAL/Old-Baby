package com.oldbaby.common.bean;

import com.oldbaby.oblib.OrmDto;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/28
 * update time:
 * email: 723526676@qq.com
 */
public class PageItem extends OrmDto {

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    
    public int type;
    public String text;
    public String imageUrl;
}
