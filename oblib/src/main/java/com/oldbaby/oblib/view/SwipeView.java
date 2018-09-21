package com.oldbaby.oblib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;

/**
 * TODO 趁早替换成ViewPager
 */
public class SwipeView extends HorizontalScrollView {

    /**
     * 用来解决嵌套的问题
     */
    public static interface InterceptListener {

        void onInterceptTouchDown();

        void onInterceptTouchUp();

        void moveBack();

        void movePre();
    }

    public InterceptListener interceptListener;
    public boolean isScrollable = true;

    public String TAG = "swipe";
    private static int DEFAULT_SWIPE_THRESHOLD = 60;

    private LinearLayout mLinearLayout;
    private Context mContext;
    private int SCREEN_WIDTH;
    private int mMotionStartX;
    private int mMotionStartY;
    private boolean mMostlyScrollingInX = false;
    private boolean mMostlyScrollingInY = false;
    private boolean mJustInterceptedAndIgnored = false;
    // protected boolean mCallScrollToPageInOnLayout = false;
    private int mCurrentPage = 0;
    private int mPageWidth = 0;
    private OnPageChangedListener mOnPageChangedListener = null;
    private SwipeOnTouchListener mSwipeOnTouchListener;
    private View.OnTouchListener mOnTouchListener;
    private PageControl mPageControl = null;

    /**
     * {@inheritDoc}
     */

    public SwipeView(Context context) {
        super(context);
        mContext = context;
        initSwipeView();
    }

    /**
     * {@inheritDoc}
     */
    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initSwipeView();
    }

    /**
     * {@inheritDoc}
     */
    public SwipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initSwipeView();
    }

    private void initSwipeView() {
        mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        super.addView(mLinearLayout, -1, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        setSmoothScrollingEnabled(true);
        setHorizontalFadingEdgeEnabled(false);
        setHorizontalScrollBarEnabled(false);

        Display display = ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        SCREEN_WIDTH = (int) (display.getWidth());
        mPageWidth = SCREEN_WIDTH;
        mCurrentPage = 0;

        mSwipeOnTouchListener = new SwipeOnTouchListener();
        super.setOnTouchListener(mSwipeOnTouchListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception ex) {
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllViews() {
        requestLayout();
        invalidate();
        mLinearLayout.removeAllViews();
    }

    public void removeView(View view) {
        requestLayout();
        invalidate();
        mLinearLayout.removeView(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child) {
        this.addView(child, -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child, int index) {
        ViewGroup.LayoutParams params;
        if (child.getLayoutParams() == null) {
            params = new LayoutParams(mPageWidth, LayoutParams.MATCH_PARENT);
        } else {
            params = child.getLayoutParams();
            params.width = mPageWidth;
        }
        this.addView(child, index, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        params.width = mPageWidth;
        this.addView(child, -1, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        requestLayout();
        invalidate();
        mLinearLayout.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setPageWidth(getMeasuredWidth());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mCurrentPage * mPageWidth, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    /**
     * Get the View object that contains all the children of this SwipeView. The
     * same as calling getChildAt(0) A SwipeView behaves slightly differently
     * from a normal ViewGroup, all the children of a SwipeView sit within a
     * LinearLayout, which then sits within the SwipeView object.
     *
     * @return linearLayout The View object that contains all the children of
     * this view
     */
    public LinearLayout getChildContainer() {
        return mLinearLayout;
    }

    /**
     * Get the swiping threshold distance to make the screens change
     *
     * @return swipeThreshold The minimum distance the finger should move to
     * allow the screens to change
     */
    public int getSwipeThreshold() {
        return DEFAULT_SWIPE_THRESHOLD;
    }

    /**
     * Set the swiping threshold distance to make the screens change
     *
     * @param swipeThreshold The minimum distance the finger should move to allow the
     *                       screens to change
     */
    public void setSwipeThreshold(int swipeThreshold) {
        DEFAULT_SWIPE_THRESHOLD = swipeThreshold;
    }

    /**
     * Get the current page the SwipeView is on
     *
     * @return The current page the SwipeView is on
     */
    public int getCurrentPage() {
        return mCurrentPage;
    }

    /**
     * Return the number of pages in this SwipeView
     *
     * @return Returns the number of pages in this SwipeView
     */
    public int getPageCount() {
        return mLinearLayout.getChildCount();
    }

    /**
     * Go directly to the specified page
     *
     * @param page The page to scroll to
     */
    public void scrollToPage(int page) {
        scrollToPage(page, false, true);
    }

    /**
     * Animate a scroll to the specified page
     *
     * @param page The page to animate to
     */
    public void smoothScrollToPage(int page) {
        scrollToPage(page, true, true);
    }

    public void smoothScrollToPage(int page, boolean callback) {
        scrollToPage(page, true, callback);
    }

    private void scrollToPage(int page, boolean smooth, boolean callback) {

        int oldPage = mCurrentPage;
        if (page >= getPageCount() && getPageCount() > 0) {
            page--;
        } else if (page < 0) {
            page = 0;
        }

        if (mOnPageChangedListener != null && oldPage != page
                && !mOnPageChangedListener.shouldSelect(oldPage, page)) {
            if (smooth) {
                smoothScrollTo(mCurrentPage * mPageWidth, 0);
            } else {
                scrollTo(mCurrentPage * mPageWidth, 0);
            }
            return;
        }

        if (smooth) {
            smoothScrollTo(page * mPageWidth, 0);
        } else {
            scrollTo(page * mPageWidth, 0);
        }
        mCurrentPage = page;

        if (mOnPageChangedListener != null && oldPage != page && callback) {
            mOnPageChangedListener.onPageChanged(oldPage, page);
        }
        if (mPageControl != null && oldPage != page) {
            mPageControl.setCurrentPage(page);
        }

        // mCallScrollToPageInOnLayout = !mCallScrollToPageInOnLayout;
    }

    /**
     * Set the width of each page. This function returns an integer that should
     * be added to the left margin of the first child and the right margin of
     * the last child. This enables all the children to appear to be central
     *
     * @param pageWidth The width you wish to assign for each page
     * @return An integer to add to the left margin of the first child and the
     * right margin of the last child
     */
    public int setPageWidth(int pageWidth) {
        mPageWidth = pageWidth;
        for (int i = 0, count = mLinearLayout.getChildCount(); i < count; i++) {
            View child = mLinearLayout.getChildAt(i);
            LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) child
                    .getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(mPageWidth,
                        LayoutParams.MATCH_PARENT);
            } else {
                params.width = mPageWidth;
            }
        }
        return (SCREEN_WIDTH - mPageWidth) / 2;
    }

    /**
     * Set the width of each page by using the layout parameters of a child.
     * Call this function before you add the child to the SwipeView to maintain
     * the child's size. This function returns an integer that should be added
     * to the left margin of the first child and the right margin of the last
     * child. This enables all the children to appear to be central
     *
     * @param childLayoutParams A child view that you have added / will add to the SwipeView
     * @return An integer to add to the left margin of the first child and the
     * right margin of the last child
     */
    public int calculatePageSize(MarginLayoutParams childLayoutParams) {
        return setPageWidth(childLayoutParams.leftMargin
                + childLayoutParams.width + childLayoutParams.rightMargin);
    }

    /**
     * Return the current width of each page
     *
     * @return Returns the width of each page
     */
    public int getPageWidth() {
        return mPageWidth;
    }

    /**
     * Assign a PageControl object to this SwipeView. Call after adding all the
     * children
     *
     * @param pageControl The PageControl object to assign
     */
    public void setPageControl(PageControl pageControl) {
        mPageControl = pageControl;

        pageControl.setPageCount(getPageCount());
        pageControl.setCurrentPage(mCurrentPage);
        pageControl
                .setOnPageControlClickListener(new PageControl.OnPageControlClickListener() {
                    public void goForwards() {
                        smoothScrollToPage(mCurrentPage + 1);
                    }

                    public void goBackwards() {
                        smoothScrollToPage(mCurrentPage - 1);
                    }
                });
    }

    /**
     * Return the current PageControl object
     *
     * @return Returns the current PageControl object
     */
    public PageControl getPageControl() {
        return mPageControl;
    }

    /**
     * Implement this listener to listen for page change events
     *
     * @author Jason Fry - jasonfry.co.uk
     */
    public static interface OnPageChangedListener {

        public boolean shouldSelect(int oldPage, int newPage);

        /**
         * Event for when a page changes
         *
         * @param oldPage The page the view was on previously
         * @param newPage The page the view has moved to
         */
        public void onPageChanged(int oldPage, int newPage);
    }

    /**
     * Set the current OnPageChangedListsner
     *
     * @param onPageChangedListener The OnPageChangedListener object
     */
    public void setOnPageChangedListener(
            OnPageChangedListener onPageChangedListener) {
        mOnPageChangedListener = onPageChangedListener;
    }

    /**
     * Get the current OnPageChangeListsner
     *
     * @return The current OnPageChangedListener
     */
    public OnPageChangedListener getOnPageChangedListener() {
        return mOnPageChangedListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (interceptListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    interceptListener.onInterceptTouchDown();
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    interceptListener.onInterceptTouchUp();
                    break;
                }
            }

        }

        if (interceptListener == null
                && ev.getAction() == MotionEvent.ACTION_DOWN) {
            isScrollable = true;
        }

        if (!isScrollable)
            return false;

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mMotionStartX = (int) ev.getX();
            mMotionStartY = (int) ev.getY();
            if (!mJustInterceptedAndIgnored) {
                mMostlyScrollingInX = false;
                mMostlyScrollingInY = false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            detectMostlyScrollingDirection(ev);
        } else if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mJustInterceptedAndIgnored = false;
        }

        if (mMostlyScrollingInY) {
            mJustInterceptedAndIgnored = false;
            return false;
        }
        if (mMostlyScrollingInX) {
            mJustInterceptedAndIgnored = true;
            return true;
        }

        return false;
    }

    private void detectMostlyScrollingDirection(MotionEvent ev) {
        if (!mMostlyScrollingInX && !mMostlyScrollingInY) // if we dont know
        // which direction
        // we're going yet
        {
            float xDistance = Math.abs(mMotionStartX - ev.getX());
            float yDistance = Math.abs(mMotionStartY - ev.getY());

            if (yDistance > DensityUtil.dip2px(15) && yDistance > xDistance / 2) {
                mMostlyScrollingInY = true;
            } else if (xDistance > DensityUtil.dip2px(15) && xDistance > yDistance * 2) {
                mMostlyScrollingInX = true;
            }
        }
    }

    private class SwipeOnTouchListener implements View.OnTouchListener {
        private boolean mSendingDummyMotionEvent = false;
        private int mDistanceX;
        private int mPreviousDirection;
        private boolean mFirstMotionEvent = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (mOnTouchListener != null && !mJustInterceptedAndIgnored
                    || mOnTouchListener != null && mSendingDummyMotionEvent) {
                if (mOnTouchListener.onTouch(v, event)) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // need to call the actionUp directly so the view is not
                        // left between pages.
                        actionUp(event);
                    }
                    return true;
                }
            }

            if (mSendingDummyMotionEvent) {
                MLog.d(TAG, "return since mSendingDummyMotionEvent");
                mSendingDummyMotionEvent = false;
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    MLog.d(TAG, "ACTION_DOWN");
                    return actionDown(event);

                case MotionEvent.ACTION_MOVE:
                    MLog.d(TAG, "ACTION_MOVE");

                    return actionMove(event);

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    MLog.d(TAG, "ACTION_UP");

                    return actionUp(event);
            }

            MLog.d(TAG,
                    "do nothing with: " + event.toString() + "=="
                            + event.getAction());
            return false;
        }

        private boolean actionDown(MotionEvent event) {
            mMotionStartX = (int) event.getX();
            mMotionStartY = (int) event.getY();
            mFirstMotionEvent = false;
            return false;
        }

        private boolean actionMove(MotionEvent event) {
            int newDistance = mMotionStartX - (int) event.getX();
            int newDirection;

            if (newDistance < 0) { // backwards
                newDirection = (mDistanceX + 4 <= newDistance) ? 1 : -1;
                if (interceptListener != null) {
                    interceptListener.moveBack();
                }
            } else {// forwards
                newDirection = (mDistanceX - 4 <= newDistance) ? 1 : -1;
                if (interceptListener != null) {
                    interceptListener.movePre();
                }
            }

            if (newDirection != mPreviousDirection && !mFirstMotionEvent) {
                mMotionStartX = (int) event.getX();
                mDistanceX = mMotionStartX - (int) event.getX();
            } else {
                mDistanceX = newDistance;
            }

            mPreviousDirection = newDirection; // backwards -1, forwards is 1,

            if (mJustInterceptedAndIgnored) {
                mSendingDummyMotionEvent = true;
                MotionEvent e = MotionEvent.obtain(event.getDownTime(),
                        event.getEventTime(), MotionEvent.ACTION_DOWN,
                        mMotionStartX, mMotionStartY, event.getPressure(),
                        event.getSize(), event.getMetaState(),
                        event.getXPrecision(), event.getYPrecision(),
                        event.getDeviceId(), event.getEdgeFlags());
                dispatchTouchEvent(e);
                mJustInterceptedAndIgnored = false;

                return true;
            }
            return false;
        }

        private boolean actionUp(MotionEvent event) {

            if (interceptListener != null) {
                interceptListener.onInterceptTouchUp();
            }

            float fingerUpPosition = getScrollX();
            float numberOfPages = mLinearLayout.getMeasuredWidth() / mPageWidth;
            float fingerUpPage = fingerUpPosition / mPageWidth;
            float edgePosition = 0;

            if (mPreviousDirection == 1) // forwards
            {
                if (mDistanceX > DEFAULT_SWIPE_THRESHOLD)// if over then go
                // forwards
                {
                    if (mCurrentPage < (numberOfPages - 1))// if not at the end
                    // of the pages, you
                    // don't want to try
                    // and advance into
                    // nothing!
                    {
                        edgePosition = (int) (fingerUpPage + 1) * mPageWidth;
                    } else {
                        edgePosition = (int) (mCurrentPage) * mPageWidth;
                    }
                } else // return to start position
                {
                    if (Math.round(fingerUpPage) == numberOfPages - 1)// if at
                    // the
                    // end
                    {
                        // need to correct for when user starts to scroll into
                        // nothing then pulls it back a bit, this becomes a
                        // kind of forwards scroll instead
                        edgePosition = (int) (fingerUpPage + 1) * mPageWidth;
                    } else // carry on as normal
                    {
                        edgePosition = mCurrentPage * mPageWidth;
                    }
                }

            } else // backwards
            {
                if (mDistanceX < -DEFAULT_SWIPE_THRESHOLD)// go backwards
                {
                    edgePosition = (int) (fingerUpPage) * mPageWidth;
                    if (fingerUpPage <= 0 && mOnPageChangedListener != null) {
                        mOnPageChangedListener.shouldSelect(0, -1);
                    }
                } else // return to start position
                {
                    if (Math.round(fingerUpPage) == 0)// if at beginning,
                    // correct
                    {
                        // need to correct for when user starts to scroll into
                        // nothing then pulls it back a bit, this becomes a
                        // kind of backwards scroll instead
                        edgePosition = (int) (fingerUpPage) * mPageWidth;
                    } else // carry on as normal
                    {
                        edgePosition = mCurrentPage * mPageWidth;
                    }

                }
            }

            smoothScrollToPage((int) edgePosition / mPageWidth);
            mFirstMotionEvent = true;
            mDistanceX = 0;
            mMostlyScrollingInX = false;
            mMostlyScrollingInY = false;
            mJustInterceptedAndIgnored = false;

            return true;
        }
    }

}