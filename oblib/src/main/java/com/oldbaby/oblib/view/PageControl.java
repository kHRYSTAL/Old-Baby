package com.oldbaby.oblib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.util.MLog;

import java.util.ArrayList;

public class PageControl extends LinearLayout implements View.OnTouchListener {

    public static final String TAG = "pagecontrol";

    protected int inActiveSize = 7;
    protected int activeSize = 14;
    protected Drawable activeDrawable;
    protected Drawable inactiveDrawable;
    protected int mPageCount = 0;
    protected int mCurrentPage = -1;

    private ArrayList<View> indicators;
    private final Context mContext;
    private OnPageControlClickListener mOnPageControlClickListener = null;
    private Scroller mScroller = null;

    public PageControl(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PageControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();

    }

    protected void init() {

        mScroller = new Scroller(mContext);
        indicators = new ArrayList<View>();

        setStyle();

        setOnTouchListener(this);
    }

    public void setStyle() {
        activeDrawable = new ShapeDrawable();
        inactiveDrawable = new ShapeDrawable();

        activeDrawable.setBounds(0, 0, activeSize, activeSize);
        inactiveDrawable.setBounds(0, 0, inActiveSize, inActiveSize);

        Shape s1 = new OvalShape();
        s1.resize(activeSize, activeSize);

        Shape s2 = new OvalShape();
        s2.resize(inActiveSize, inActiveSize);

        int i[] = new int[2];
        i[0] = android.R.attr.textColorSecondaryInverse;
        i[1] = android.R.attr.textColorSecondary;
        TypedArray a = mContext.getTheme().obtainStyledAttributes(i);

        ((ShapeDrawable) activeDrawable).getPaint().setColor(
                mContext.getResources().getColor(R.color.chat_indicator_select));
        ((ShapeDrawable) inactiveDrawable).getPaint().setColor(
                mContext.getResources().getColor(R.color.chat_indicator_unselect));

        ((ShapeDrawable) activeDrawable).setShape(s1);
        ((ShapeDrawable) inactiveDrawable).setShape(s2);

        inActiveSize = (int) (inActiveSize * getResources().getDisplayMetrics().density);
        activeSize = (int) (activeSize * getResources().getDisplayMetrics().density);
    }

    /**
     * 设置选中状态颜色，设置非选中状态颜色
     *
     * @param active
     * @param inactive
     */
    public void setColors(int active, int inactive) {
        ((ShapeDrawable) activeDrawable).getPaint().setColor(active);
        ((ShapeDrawable) inactiveDrawable).getPaint().setColor(inactive);
        postInvalidate();
    }

    public void reset() {
        this.mCurrentPage = -1;
        this.mPageCount = 0;
        this.indicators.clear();
        this.removeAllViews();
    }

    public void setPageCount(int pageCount) {

        reset();
        mPageCount = pageCount;

        for (int i = 0; i < pageCount; i++) {
            View view = getView(i + "");
            indicators.add(view);
            addView(view);
        }
    }

    public View getView(String i) {
        TextView imageView = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                inActiveSize, inActiveSize);
        params.setMargins(inActiveSize / 2, inActiveSize / 3, inActiveSize / 2,
                inActiveSize / 3);
        imageView.setLayoutParams(params);
        imageView.setGravity(Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL);
        imageView.setTextColor(Color.WHITE);
        imageView.setTextSize(5);
        imageView.getPaint().setFakeBoldText(true);
        imageView.setBackgroundDrawable(inactiveDrawable);
        return imageView;
    }

    private boolean needLayout = false;

    public void setCurrentPage(int currentPage) {
        if (-1 < currentPage && currentPage < mPageCount) {
            View toInActive = null;
            if (mCurrentPage >= 0) {
                toInActive = indicators.get(mCurrentPage);
            }

            mCurrentPage = currentPage;
            View toActive = indicators.get(mCurrentPage);
            MLog.d(TAG, toActive.getLeft() + ":" + toActive.getRight() + "=="
                    + getWidth());

            pageChanged(toActive, toInActive);
        } else if (-1 < mCurrentPage && mCurrentPage < mPageCount) {
            pageChanged(null, indicators.get(mCurrentPage));
        }
        needLayout = true;
        requestLayout();
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (needLayout && mCurrentPage >= 0 && mCurrentPage < mPageCount) {
            needLayout = false;
            // scroll page control to show entire view
            View toActive = indicators.get(mCurrentPage);
            int activeLeft = toActive.getLeft();
            int activeRight = toActive.getRight();
            int maxRight = indicators.get(indicators.size() - 1).getRight()
                    + inActiveSize / 2;
            int scrollPageWidth = getWidth() - inActiveSize * 2;

            // over left, scroll to right
            if (activeLeft - inActiveSize / 2 < getScrollX()) {
                if (getScrollX() > getWidth()) {
                    smoothScroll(-scrollPageWidth);
                    // scrollBy(-scrollPageWidth, 0);
                    MLog.d(TAG, "scroll right by: " + (-scrollPageWidth));
                } else {
                    smoothScroll(0 - getScrollX());
                    // scrollTo(0, 0);
                    MLog.d(TAG, "scroll to 0");
                }
            }

            // over right, scroll to left
            if ((activeRight + inActiveSize / 2) > (getScrollX() + getWidth())) {

                if ((maxRight - (getScrollX() + getWidth())) > getWidth()) {
                    smoothScroll(scrollPageWidth);
                    // scrollBy(scrollPageWidth, 0);
                    MLog.d(TAG, "scroll left by: " + scrollPageWidth);
                } else {
                    smoothScroll(maxRight - getWidth() - getScrollX());
                    // scrollTo(maxRight - getWidth(), 0);
                    MLog.d(TAG, "scroll to:" + maxRight);
                }
            }
        }

    }

    protected void pageChanged(View toActive, View toInActive) {
        if (toInActive != null) {
            toInActive.setBackgroundDrawable(inactiveDrawable);
        }
        if (toActive != null) {
            toActive.setBackgroundDrawable(activeDrawable);
        }
    }

    /**
     * DO NOT use this method, just for spring festival release
     */
    public void revertStatuIndicators() {
        Drawable tmp = inactiveDrawable;
        inactiveDrawable = activeDrawable;
        activeDrawable = tmp;
    }

    public interface OnPageControlClickListener {

        public abstract void goForwards();

        public abstract void goBackwards();
    }

    public void setOnPageControlClickListener(
            OnPageControlClickListener onPageControlClickListener) {
        mOnPageControlClickListener = onPageControlClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnPageControlClickListener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:

                    if (PageControl.this.getOrientation() == LinearLayout.HORIZONTAL) {
                        if (event.getX() < (PageControl.this.getWidth() / 2)) {
                            if (mCurrentPage > 0) {
                                mOnPageControlClickListener.goBackwards();
                            }
                        } else // if on right of view
                        {
                            if (mCurrentPage < (mPageCount - 1)) {
                                mOnPageControlClickListener.goForwards();
                            }
                        }
                    } else {
                        if (event.getY() < (PageControl.this.getHeight() / 2)) {
                            if (mCurrentPage > 0) {
                                mOnPageControlClickListener.goBackwards();
                            }
                        } else // if on bottom half of view
                        {
                            if (mCurrentPage < (mPageCount - 1)) {
                                mOnPageControlClickListener.goForwards();
                            }
                        }
                    }

                    return false;
            }
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // 产生了动画效果 每次滚动一点
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 刷新View 否则效果可能有误差
            postInvalidate();
        }
    }

    private void smoothScroll(int dx) {
        mScroller.startScroll(getScrollX(), 0, dx, 0, 300);
        invalidate();
    }

}