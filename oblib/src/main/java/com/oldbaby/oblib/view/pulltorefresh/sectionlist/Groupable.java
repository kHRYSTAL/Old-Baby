package com.oldbaby.oblib.view.pulltorefresh.sectionlist;

import java.util.List;

public interface Groupable<C> {

    String getTitle();

    List<C> getChildren();

    void addChild(C child);

    void addChildren(List<C> children);

}
