package com.oldbaby.oblib.component.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.oldbaby.oblib.util.MLog;

public abstract class CatchableOnHierarchyChangeListener implements
        ViewGroup.OnHierarchyChangeListener {
    public abstract void intlOnChildViewAdded(View parent, View child);

    public abstract void intlOnChildViewRemoved(View parent, View child);

    @Override
    public void onChildViewAdded(View parent, View child) {
        try {
            intlOnChildViewAdded(parent, child);
        } catch (final Error t) {
            MLog.e("CatchableOnHierarchyChangeListener", t.getMessage(), t);

            throw (Error) t.fillInStackTrace();
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        try {
            intlOnChildViewRemoved(parent, child);
        } catch (final Error t) {
            MLog.e("CatchableOnHierarchyChangeListener", t.getMessage(), t);

            throw (Error) t.fillInStackTrace();
        }
    }
}