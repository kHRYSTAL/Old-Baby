package com.oldbaby.oblib.view.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public class TabBarView extends FrameLayout implements View.OnClickListener {


    private static final String TAG = "tab";
    private static final int TAG_OFFSET = 1000;
    private static final int DIVIDER_WIDTH = DensityUtil.dip2px(1);
    private static final int MARGIN_INDICATOR = 40;

    private static final int MODE_AVG = 0, MODE_WRAP = 1, MODE_WRAP_FILL = 2;

    private Drawable bottomIndicatorDrawable = null;
    private int mode = 0;

    private ArrayList<Integer> tabIdArray = null;

    private TabBarViewListener tabListener;
    private TabBarOnCreateListener createListener;

    /**
     * container of tab views, now it is Button
     */
    private LinearLayout tabButtonsContainer;
    private ImageView bottomIndicator;
    private View bottomDivider;
    private FrameLayout.LayoutParams dividerLayout;
    private ObservableHorizontalScrollView scrollView;

    private ImageView leftIndicator;
    private ImageView rightIndicator;

    private ArrayList<? extends TabInfo> tabs;
    private int selectedIndex = -1;

    private boolean inAnimation = false;
    private boolean isBottomIndicatorEnabled = true;

    private final int[] tabWidth = new int[500]; // record each tab's width
    private final int[] tabFromX = new int[500]; // record each tab's start X
    // position
    private final int[] tabToX = new int[500]; // record each tab's end X
    // position
    // record all tab's view
    private final HashMap<TabInfo, View> origTabViews = new HashMap<TabInfo, View>();
    private final ArrayList<LinearLayout> tabViews = new ArrayList<LinearLayout>();
    private final HashMap<View, Integer> hmTabIndexMapping = new HashMap<View, Integer>();
    private int tabCount;
    private int totalWidth;

    private boolean enableDivider = false;

    public TabBarView(Context context) {
        super(context);
        bottomIndicatorDrawable = context.getResources().getDrawable(
                R.drawable.tab_ind_for_three);
        commInit(context, null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TabBar);
        if (a.hasValue(R.styleable.ImageText_itSrc)) {
            bottomIndicatorDrawable = a
                    .getDrawable(R.styleable.TabBar_tbIndicator);
        } else {
            bottomIndicatorDrawable = context.getResources().getDrawable(
                    R.drawable.tab_ind_for_three);
        }
        if (a.hasValue(R.styleable.TabBar_tbMode)) {
            this.mode = a.getInt(R.styleable.TabBar_tbMode, MODE_AVG);
        }
        if (a.hasValue(R.styleable.TabBar_tbDivider)) {
            this.enableDivider = a.getBoolean(R.styleable.TabBar_tbDivider,
                    enableDivider);
        }
        a.recycle();

        commInit(context, attrs);
    }

    /**
     * re-set tabs, which will clear former tabs
     *
     * @param tabs
     */
    public void setTabs(ArrayList<? extends TabInfo> tabs) {

        if (tabs == null || tabs.size() < 1) {
            return;
        }
        this.clearTabs();
        this.tabs = tabs;
        this.initTabs(tabs);
        this.addTabButtons();
        this.relayout();
    }

    private void clearTabs() {
        this.tabCount = -1;
        this.selectedIndex = -1;
        resetArray(tabFromX);
        resetArray(tabToX);
        resetArray(tabWidth);
        this.tabViews.clear();
        this.hmTabIndexMapping.clear();
        tabButtonsContainer.removeAllViews();
    }

    private void initTabs(ArrayList<? extends TabInfo> tabs) {
        this.tabCount = tabs.size();
        this.selectedIndex = -1;
    }

    /**
     * add tab view from tabs property
     */
    private void addTabButtons() {
        int index = 0;
        for (TabInfo tab : tabs) {

            int id = TAG_OFFSET + index;

            if (tabIdArray == null) {
                tabIdArray = new ArrayList<Integer>();
            }
            tabIdArray.add(tab.tabId);

            LinearLayout ll = new LinearLayout(getContext());
            ll.setId(id);
            ll.setOnClickListener(this);
            ll.setId(TAG_OFFSET + index);
            ll.setGravity(Gravity.CENTER);
            View tabView;
            if (createListener != null) {
                tabView = this.createListener.createTabView(this, tab, index);
            } else {
                tabView = getTabView(tab, index);
            }
            tabView.setOnClickListener(this);
            tabView.setId(id);
            origTabViews.put(tab, tabView);
            if (tabView.getLayoutParams() != null) {
                ll.addView(tabView);
            } else {
                ll.addView(tabView, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
            }

            tabViews.add(ll);
            hmTabIndexMapping.put(ll, index);

            if (index > 0 && enableDivider) {
                View divider = this.getDividerView();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        DIVIDER_WIDTH, DensityUtil.dip2px(12));
                int dp9 = DensityUtil.dip2px(9);
                params.setMargins(0, dp9, 0, dp9);
                params.gravity = Gravity.CENTER_VERTICAL;
                divider.setLayoutParams(params);
                tabButtonsContainer.addView(divider);
            }

            tabButtonsContainer.addView(ll);
            ++index;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (tabCount > 0) {
            MLog.d(TAG, "meature and layout tab buttons");
            int specSize = MeasureSpec.getSize(widthMeasureSpec);

            this.meatureTabButtons(specSize);
            this.layoutTabButtons(specSize);

            if (mode == MODE_WRAP) {
                int actualWidth = getActualWidth();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(actualWidth,
                        MeasureSpec.EXACTLY);
            } else if (mode == MODE_WRAP_FILL) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(specSize,
                        MeasureSpec.EXACTLY);
            }

            if (selectedIndex < 0) {
                MLog.d(TAG, "meature and layout tab buttons");
                setSelectedIndex(0, false, true);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT <= 7) {
            this.hack21();
        }
    }

    private int getActualWidth() {
        int count = tabViews.size();

        int totalViewWidth = 0;
        int[] viewWidths = new int[count];

        int mwidth = 0, mh = 0;
        for (int i = 0; i < count; i++) {
            View tabView = tabViews.get(i);
            measureChild(tabView, mwidth, mh);
            mwidth = tabView.getMeasuredWidth();
            mh = tabView.getMeasuredHeight();
            viewWidths[i] = mwidth;
            totalViewWidth += mwidth;
        }

        int curWidth;
        if (enableDivider) {
            curWidth = totalViewWidth + (tabCount - 1) * DIVIDER_WIDTH;
        } else {
            curWidth = totalViewWidth;
        }
        return curWidth;
    }

    private void meatureTabButtons(int viewWidth) {

        int count = tabViews.size();

        int totalViewWidth = 0;
        int[] viewWidths = new int[count];

        int mwidth = 0, mh = 0;
        for (int i = 0; i < count; i++) {
            View tabView = tabViews.get(i);
            ViewGroup.LayoutParams lp = tabView.getLayoutParams();
            if (lp != null) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            measureChild(tabView, mwidth, mh);
            mwidth = tabView.getMeasuredWidth();
            mh = tabView.getMeasuredHeight();
            viewWidths[i] = mwidth;
            totalViewWidth += mwidth;
        }

        int curWidth;
        if (enableDivider) {
            curWidth = totalViewWidth + (tabCount - 1) * DIVIDER_WIDTH;
        } else {
            curWidth = totalViewWidth;
        }

        totalWidth = 0;
        int padding = 0;
        if (mode == MODE_AVG) {
            if (curWidth < viewWidth) {
                // tabs cannot fill container width, automatically add some
                // padding
                padding = (viewWidth - curWidth) / tabCount;
                totalWidth = viewWidth;
            } else {
                totalWidth = curWidth;
            }
        } else {
            totalWidth = curWidth;
        }

        int fromX = 0; // indicate the buttons
        for (int i = 0; i < tabCount; i++) {

            int width = 0;
            if (i == count - 1) {
                width = totalWidth - fromX;
            } else {
                width = padding + viewWidths[i];
            }

            tabWidth[i] = width;
            tabFromX[i] = fromX;
            tabToX[i] = fromX + width;
            if (enableDivider) {
                fromX += (DIVIDER_WIDTH + width);
            } else {
                fromX += width;
            }
        }

    }

    /**
     * calculate all tab views' sizes
     */
    private void layoutTabButtons(int viewWidth) {
        // MLog.d(TAG, "layout tab buttons");
        for (int i = 0; i < tabCount; i++) {
            int width = tabWidth[i];
            View ll = tabViews.get(i);

            LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(
                    width, LinearLayout.LayoutParams.FILL_PARENT);
            btnLayout.gravity = Gravity.CENTER_VERTICAL;
            // btnLayout.topMargin = bottomIndicator.getHeight() * 2 / 3;
            ll.setLayoutParams(btnLayout);
            // MLog.d(TAG, "width i is: " + width);
        }

        if (totalWidth > viewWidth + scrollView.getScrollX()) {
            rightIndicator.setVisibility(View.VISIBLE);
        }

    }

    /**
     * construct tab bar views.
     *
     * @param context
     */
    // layout structure
    // --viewgroup
    // ------leftindicator
    // ------scroll
    // ----------frame
    // --------------linearlayout
    // --------------bottom_divider
    // --------------bottomindicator
    // ------rightindicator
    private void commInit(Context context, AttributeSet attrs) {
        FrameLayout frame = new FrameLayout(context);
        frame.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        tabButtonsContainer = new LinearLayout(context);
        FrameLayout.LayoutParams btnContainerLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.FILL_PARENT);

        tabButtonsContainer.setLayoutParams(btnContainerLayout);
        tabButtonsContainer.setOrientation(LinearLayout.HORIZONTAL);

        frame.addView(tabButtonsContainer);

        bottomDivider = new View(context);
        dividerLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(0.7f));
        dividerLayout.gravity = Gravity.BOTTOM;
        bottomDivider.setLayoutParams(dividerLayout);

        frame.addView(bottomDivider);

        bottomIndicator = new ImageView(context);
        bottomIndicator.setImageDrawable(bottomIndicatorDrawable);
        FrameLayout.LayoutParams indicatorImageLayout = new FrameLayout.LayoutParams(
                0, FrameLayout.LayoutParams.WRAP_CONTENT);
        indicatorImageLayout.gravity = Gravity.LEFT | Gravity.BOTTOM;
        bottomIndicator.setLayoutParams(indicatorImageLayout);
        bottomIndicator.setScaleType(ImageView.ScaleType.FIT_XY);

        frame.addView(bottomIndicator);

        scrollView = new ObservableHorizontalScrollView(context);
        FrameLayout.LayoutParams scrollViewLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.FILL_PARENT);
        scrollView.setLayoutParams(scrollViewLayout);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.addView(frame);

        scrollView.setScrollViewListener(new ScrollViewListener() {

            @Override
            public void onScrollChanged(View scrollView, int x, int y,
                                        int oldx, int oldy) {
                verifyLeftRightIndicator();
            }

        });

        this.addView(scrollView);

        leftIndicator = new ImageView(context);
//        leftIndicator.setImageResource(R.drawable.tabbar_left);
        leftIndicator.setScaleType(ImageView.ScaleType.FIT_XY);
        FrameLayout.LayoutParams leftIndicatorLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.FILL_PARENT);
        leftIndicatorLayout.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        leftIndicator.setLayoutParams(leftIndicatorLayout);
        leftIndicator.setVisibility(View.INVISIBLE);
        this.addView(leftIndicator);

        rightIndicator = new ImageView(context);
        rightIndicator.setScaleType(ImageView.ScaleType.FIT_XY);
//        rightIndicator.setImageResource(R.drawable.tabbar_right);
        FrameLayout.LayoutParams rightIndicatorLayout = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.FILL_PARENT);
        rightIndicatorLayout.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        rightIndicator.setLayoutParams(rightIndicatorLayout);
        rightIndicator.setVisibility(View.INVISIBLE);
        this.addView(rightIndicator);

    }

    /**
     * judge if display left or right indicator
     */
    private void verifyLeftRightIndicator() {
        int contentWidth = scrollView.getChildAt(0).getMeasuredWidth();
        int offsetX = scrollView.getScrollX();
        if (offsetX > 0) {
            leftIndicator.setVisibility(View.VISIBLE);
        } else {
            leftIndicator.setVisibility(View.INVISIBLE);
        }

        if (contentWidth <= offsetX + scrollView.getWidth()) {
            rightIndicator.setVisibility(View.INVISIBLE);
        } else {
            rightIndicator.setVisibility(View.VISIBLE);
        }
    }

    // =========================================
    // =============public methods=================

    public void setBottomIndicator(Drawable drawable) {
        this.bottomIndicatorDrawable = drawable;
        this.bottomIndicator.setImageDrawable(bottomIndicatorDrawable);
    }

    public void setBottomIndicator(boolean enabled) {
        this.isBottomIndicatorEnabled = enabled;
    }

    public void setBottomDividerColor(int color) {
        bottomDivider.setBackgroundColor(color);
    }

    public void setBottomDividerRes(int resId, int height) {
        dividerLayout.height = height;
        bottomDivider.setBackgroundResource(resId);
    }

    /**
     * @param left  edge indicator's resource id
     * @param right right edge indicator's resource id
     */
    public void setEdgeIndicator(int left, int right) {
        leftIndicator.setImageResource(left);
        rightIndicator.setImageResource(right);
    }

    public void setListener(TabBarViewListener l) {
        this.tabListener = l;
    }

    public void setCreateListener(TabBarOnCreateListener createListener) {
        this.createListener = createListener;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void selectByTabId(int tabId) {
        if (inAnimation == false && tabIdArray != null) {
            for (int i = 0; i < tabIdArray.size(); i++) {
                if (tabIdArray.get(i) == tabId) {
                    setSelectedIndex(i, true, true);
                    MLog.d(TAG, "selectIndex");
                    return;
                }
            }
        }
    }

    public void setSelectedIndex(int newSelectedIndex, boolean animated,
                                 boolean sendMes) {

        if (newSelectedIndex < 0 || newSelectedIndex >= tabCount) {
        }

        if (!this.canSelectIndex(newSelectedIndex)) {
            return;
        }

        if (this.selectedIndex == newSelectedIndex) {
            TabInfo toTab = this.tabs.get(newSelectedIndex);
            if (sendMes) {
                sendDidSelectMessage(toTab, newSelectedIndex);
            }
            return;
        }

        TabInfo fromTab = null;
        LinearLayout lltab = null;

        if (this.selectedIndex > -1) {
            fromTab = tabs.get(selectedIndex);
            lltab = tabViews.get(selectedIndex);
            this.deselectTabButton(lltab.getChildAt(0));
        }

        int oldIndex = selectedIndex;
        this.selectedIndex = newSelectedIndex;

        TabInfo toTab = this.tabs.get(newSelectedIndex);
        LinearLayout llTab = tabViews.get(selectedIndex);
        this.selectTabButton(llTab.getChildAt(0), toTab);

        // scroll view to display entire tab view
        int scrollStartX = this.scrollView.getScrollX();
        int scrollEndX = scrollStartX + this.scrollView.getWidth();
        int btnStartX = tabFromX[selectedIndex];
        int btnEndX = tabToX[selectedIndex];
        if (btnStartX < scrollStartX) {
            this.scrollView.scrollBy(btnStartX - scrollStartX, 0);
        } else if (btnEndX > scrollEndX) {
            this.scrollView.scrollBy(btnEndX - scrollEndX, 0);
        }

        if (fromTab != null && animated) // don't animate
        {
            int fromOffset = tabFromX[oldIndex];
            int toOffset = tabFromX[selectedIndex];
            this.translateIndicator(fromOffset, toOffset, 80);
        } else {
            this.centerIndicatorOnButton(llTab, true);
        }

        if (sendMes) {
            sendDidSelectMessage(toTab, newSelectedIndex);
        }
    }

    private void sendDidSelectMessage(TabInfo toTab, int toIndex) {
        if (tabListener != null)
            tabListener.didSelectTabBar(this, toTab, toIndex);
    }

    private boolean canSelectIndex(int index) {
        boolean allowSelect = true;
        if (this.tabListener != null) {
            TabInfo toTab = this.tabs.get(index);
            allowSelect = tabListener.shouldSelectTab(this, toTab, index);
        }
        return allowSelect;
    }

    public TabInfo getSelectedTab() {
        if (this.selectedIndex != -1 && this.tabs != null)
            return this.tabs.get(this.selectedIndex);
        else
            return null;
    }

    public View getTabView(TabInfo tabinfo) {
        if (origTabViews.containsKey(tabinfo)) {
            return origTabViews.get(tabinfo);
        }
        return null;
    }

    public View getTabView(int index) {
        if (index >= 0 && index < origTabViews.size()) {
            return origTabViews.get(tabs.get(index));
        }
        return null;
    }

    private void centerIndicatorOnButton(View button, boolean relayout) {
        if (!hmTabIndexMapping.containsKey(button))
            return;

        if (isBottomIndicatorEnabled) {
            bottomIndicator.setVisibility(View.VISIBLE);
        } else {
            bottomIndicator.setVisibility(View.GONE);
        }
        int btnIndex = hmTabIndexMapping.get(button);

        int btnWidth = tabWidth[btnIndex];
        FrameLayout.LayoutParams indicatorImageLayout = (android.widget.FrameLayout.LayoutParams) bottomIndicator
                .getLayoutParams();
        if (indicatorImageLayout.width != btnWidth) {
            indicatorImageLayout.width = btnWidth - MARGIN_INDICATOR / 2;
            indicatorImageLayout.leftMargin = MARGIN_INDICATOR / 4;
            bottomIndicator.setLayoutParams(indicatorImageLayout);
            if (relayout) {
                this.relayout();
            }
        }

    }

    private void selectTabButton(View tabView, TabInfo tabInfo) {
        if (tabView == null)
            return;
        if (this.createListener != null) {
            this.createListener.selectTabView(tabView, tabInfo);
        } else {
            Button btn = (Button) tabView;
            btn.setTextColor(this.getResources()
                    .getColor(R.color.tab_highlight));
        }
    }

    private void deselectTabButton(View tabView) {

        if (tabView == null)
            return;
        if (this.createListener != null) {
            this.createListener.unSelectTabView(tabView);
        } else {
            Button btn = (Button) tabView;
            btn.setTextColor(Color.GRAY);
        }
    }

    private View getDividerView() {
        View v = new View(getContext());
        v.setBackgroundColor(Color.LTGRAY);
        return v;
    }

    private void translateIndicator(int from, int to, long duration) {
        final int leftMargin = MARGIN_INDICATOR / 4;
        TranslateAnimation animation = new TranslateAnimation(from, to, 0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                MLog.d(TAG, "anim finished");
                int selectedIndex = TabBarView.this.getSelectedIndex();
                int btnWidth = tabWidth[selectedIndex];
                FrameLayout.LayoutParams indicatorImageLayout = (android.widget.FrameLayout.LayoutParams) bottomIndicator
                        .getLayoutParams();
                if (indicatorImageLayout.width <= btnWidth - MARGIN_INDICATOR
                        / 2) {
                    indicatorImageLayout.width = btnWidth - MARGIN_INDICATOR
                            / 2;
                    indicatorImageLayout.leftMargin = leftMargin;
                    bottomIndicator.setLayoutParams(indicatorImageLayout);
                    relayout();
                }

                inAnimation = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                MLog.d(TAG, "anim started");
                inAnimation = true;
                int btnWidth = tabWidth[selectedIndex];
                FrameLayout.LayoutParams indicatorImageLayout = (android.widget.FrameLayout.LayoutParams) bottomIndicator
                        .getLayoutParams();
                if (indicatorImageLayout.width >= btnWidth - MARGIN_INDICATOR
                        / 2) {
                    indicatorImageLayout.width = btnWidth - MARGIN_INDICATOR
                            / 2;
                    indicatorImageLayout.leftMargin = leftMargin;
                    bottomIndicator.setLayoutParams(indicatorImageLayout);
                    relayout();
                }
            }

        });
        bottomIndicator.bringToFront();
        invalidate();
        bottomIndicator.startAnimation(animation);

    }

    /**
     * the position of tab in tab bar, divider view will be excluded
     */
    private Button getTabView(TabInfo tabInfo, int index) {

        Button button = new Button(this.getContext());
        button.setSingleLine();
        button.setGravity(Gravity.CENTER);
        button.setText(tabInfo.name);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(Color.GRAY);
        button.setTextSize(18);
        button.setPadding(0, 5, 0, 5);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = 20;
        params.rightMargin = 20;
        button.setLayoutParams(params);

        return button;
    }

    private void resetArray(int[] arr) {
        if (arr == null) {
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            arr[i] = -1;
        }
    }

    @Override
    public void onClick(View v) {
        if (inAnimation == false) {
            int id = v.getId();
            setSelectedIndex(id - TAG_OFFSET, true, true);
            MLog.d(TAG, "selected");
        }
    }

    /**
     * this method is only
     */
    private void hack21() {
        FrameLayout.LayoutParams indicatorImageLayout = (android.widget.FrameLayout.LayoutParams) bottomIndicator
                .getLayoutParams();
        bottomIndicator.setLayoutParams(indicatorImageLayout);
        FrameLayout.LayoutParams params = (LayoutParams) tabButtonsContainer
                .getLayoutParams();
        tabButtonsContainer.setLayoutParams(params);
        relayout();
    }

    private void relayout() {
    }
}
