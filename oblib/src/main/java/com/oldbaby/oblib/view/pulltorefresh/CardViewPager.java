package com.oldbaby.oblib.view.pulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oldbaby.oblib.util.DensityUtil;


/**
 * 卡片样式的viewpager
 */
public class CardViewPager extends ViewPager implements OnTouchListener {

    private android.widget.FrameLayout.LayoutParams flp;
    private android.widget.RelativeLayout.LayoutParams rlp;
    private int margintTop = 0;
    private int margintBottom = 0;
    private int margintLeft = DensityUtil.dip2px(35);
    private int margintRight = DensityUtil.dip2px(35);
    private android.widget.LinearLayout.LayoutParams llp;
    private boolean isInit = false;
    private int layoutSize = 0;

    public CardViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayoutParams();
    }

    public CardViewPager(Context context) {
        super(context);
        initLayoutParams();
    }

    private void initLayoutParams() {
        setOffscreenPageLimit(3);
        setPageMargin(DensityUtil.dip2px(20));
        setClipChildren(false);
    }

    @Override
    protected void onDraw(Canvas arg0) {
        super.onDraw(arg0);
        if (!isInit) {

            try {
                ViewGroup parent = ((ViewGroup) getParent());
                if (parent instanceof FrameLayout) {
                    flp = (android.widget.FrameLayout.LayoutParams) this
                            .getLayoutParams();
                    flp.setMargins(margintLeft, margintTop, margintRight,
                            margintBottom);
                    this.setLayoutParams(flp);
                } else if (parent instanceof RelativeLayout) {
                    rlp = (android.widget.RelativeLayout.LayoutParams) this
                            .getLayoutParams();
                    rlp.setMargins(margintLeft, margintTop, margintRight,
                            margintBottom);

                    this.setLayoutParams(rlp);
                } else if (parent instanceof LinearLayout) {
                    llp = (android.widget.LinearLayout.LayoutParams) this
                            .getLayoutParams();
                    llp.setMargins(margintLeft, margintTop, margintRight,
                            margintBottom);

                    this.setLayoutParams(llp);
                } else {
                    return;
                }
                parent.setClipChildren(false);
                parent.setOnTouchListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            layoutSize++;
            if (layoutSize == 2)
                isInit = true;
        }
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        super.onLayout(arg0, arg1, arg2, arg3, arg4);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        try {
            return dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
