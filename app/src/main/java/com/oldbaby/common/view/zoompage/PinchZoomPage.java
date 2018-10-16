package com.oldbaby.common.view.zoompage;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.oldbaby.R;
import com.oldbaby.common.app.PrefUtil;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.common.util.SpiderHeader;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * usage: 支持容器内所有文字控件放大的 NestedScrollView 未来文章详情页支持评论后 需要把父类改为linearlayout
 * 然后作为recyclerview的header
 * author: kHRYSTAL
 * create time: 18/9/28
 * update time:
 * email: 723526676@qq.com
 */
public class PinchZoomPage extends NestedScrollView implements View.OnClickListener {


    private static final String TAG = PinchZoomPage.class.getSimpleName();

    private static final float TEXT_SIZE_DISTANCE = 10.0f;
    private static final float MIN_TEXT_SIZE = 10.0f;
    private static final float MAX_TEXT_SIZE = 100.0f;

    private OutsideDownFrameLayout mBodyLayout;
    // 滑动事件监听器
    private List<OnScrollChangedListener> listeners = new ArrayList<OnScrollChangedListener>();
    private OnSizeChangedListener mChangedListener;

    private boolean mShowKeyboard = false;

    private static final float STEP = 200;

    private float ratio = 11.0f;

    private int baseDistance;

    private float baseRatio;

    private boolean zoomEnabled = true;

    private String referer;


    // real container
    private LinearLayout container;
    private List<TextView> textItems;
    private List<ImageView> imageItems;
    private List<View> allViews;
    private List<String> imagesUrls;

    private OnPageItemClickListener onPageItemClickListener;

    private int mDownY, mMoveY;

    public PinchZoomPage(Context context) {
        super(context);
        initView(context);
    }

    public PinchZoomPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PinchZoomPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void bindContainer(LinearLayout container) {
        this.container = container;
    }

    private void initView(Context context) {
        allViews = new ArrayList<>();
        textItems = new ArrayList<>();
        imageItems = new ArrayList<>();
        imagesUrls = new ArrayList<>();
        ratio = PrefUtil.Instance().getZoomPageTextRatio();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 当单指手势滑动时 或双指手势放大缩小时 需要return true 其它情况都交给子view去处理点击事件
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (zoomEnabled && ev.getPointerCount() == 2) {
                } else {
                    mDownY = (int) ev.getRawY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDownY = 0;
                mMoveY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (zoomEnabled && ev.getPointerCount() == 2) {

                } else {
                    mMoveY = (int) ev.getRawY();
                    //如果是非点击事件就拦截 让父布局接手onTouch 否则执行子ViewOnClick
                    if (mDownY - mMoveY > 0) {
                        final ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        MLog.e(TAG, "执行scrollView逻辑");
                        return true;
                    }
                }
                break;
        }
        if (zoomEnabled && ev.getPointerCount() == 2) {
            int action = ev.getAction();
            int pureAction = action & MotionEvent.ACTION_MASK;
            int distance = getDistance(ev);
            if (pureAction == MotionEvent.ACTION_POINTER_DOWN) {
                baseDistance = distance;
                baseRatio = ratio;
            }
            return true;
        }
        if (getScrollY() == 0 && mBodyLayout != null && (mBodyLayout.getCurrentState() == OutsideDownFrameLayout.DRAG_STATE_SHOW
                || mBodyLayout.getCurrentState() == OutsideDownFrameLayout.DRAG_STATE_MOVE))
            return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //body布局隐藏不处理
        if (mBodyLayout.getCurrentState() == OutsideDownFrameLayout.DRAG_STATE_HIDE) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPaintFlags((Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPaintFlags(~(Paint.LINEAR_TEXT_FLAG | Paint.SUBPIXEL_TEXT_FLAG));
                break;
        }
        // Must have two gestures.
        if (zoomEnabled && ev.getPointerCount() == 2) {
            int distance = getDistance(ev);
            float delta = (distance - baseDistance) / STEP;
            float multi = (float) Math.pow(2, delta);
            ratio = Math.min(100.0f, Math.max(0.1f, baseRatio * multi));
            if (ratio < 1.0f)
                ratio = 1.0f;
            setTextSize(ratio + 9);
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public void setContent(List<PageItem> pageItems) {
        container.removeAllViews();
        allViews.clear();
        textItems.clear();
        imageItems.clear();
        imagesUrls.clear();
        if (pageItems == null || pageItems.isEmpty())
            return;
        scrollTo(0, 0);
        float defaultTextSize = PrefUtil.Instance().getZoomPageTextSize();
        for (int i = 0; i < pageItems.size(); i++) {
            PageItem pi = pageItems.get(i);
            if (pi.type == PageItem.TYPE_TEXT) {
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.txt_black));
//                textView.setIncludeFontPadding(false);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = DensityUtil.dip2px(25);
                textView.setLayoutParams(params);
                textView.setText(pi.text);
                textView.setLineSpacing(0, 1.2f);
                textView.setTextSize(defaultTextSize);
                container.addView(textView);
                textItems.add(textView);
                allViews.add(textView);
                // 设置textView 所在的文字列表顺序
                textView.setTag(R.id.item_text, textItems.size() - 1);
                textView.setTag(R.id.item_view, i);
                // TODO: 18/9/30 需要重写textview 判断手势 否则点击事件会将父容器手势消费
                textView.setOnClickListener(this);
            } else if (pi.type == PageItem.TYPE_IMAGE) {
                ImageView imageView = new ImageView(getContext());
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = DensityUtil.dip2px(25);
                imageView.setLayoutParams(params);
                container.addView(imageView);
                if (StringUtil.isNullOrEmpty(this.referer))
                    Glide.with(getContext()).load(pi.imageUrl).into(imageView);
                else {
                    Headers headers = SpiderHeader.getInstance().addRefer(this.referer).build();
                    Glide.with(getContext()).load(new GlideUrl(pi.imageUrl, headers)).into(imageView);
                }
                imageItems.add(imageView);
                allViews.add(imageView);
                imagesUrls.add(pi.imageUrl);
                // 设置imageView 所在的图片列表顺序
                imageView.setTag(R.id.item_image, imageItems.size() - 1);
                imageView.setTag(R.id.item_view, i);
                imageView.setOnClickListener(this);
            }
        }
    }

    /**
     * setPaintFlags
     *
     * @param flags
     */
    public void setPaintFlags(int flags) {
        if (textItems != null && textItems.size() > 0) {
            for (int i = 0; i < textItems.size(); i++) {
                TextView textView = textItems.get(i);
                textView.setPaintFlags(textView.getPaintFlags() & flags);
            }
        }
    }

    /**
     * setTextSize
     *
     * @param size
     */
    public void setTextSize(float size) {
        if (size > MAX_TEXT_SIZE)
            size = MAX_TEXT_SIZE;
        else if (size < MIN_TEXT_SIZE)
            size = MIN_TEXT_SIZE;
        ratio = size - 9;
        PrefUtil.Instance().setZoomPageTextRatio(ratio);
        if (textItems != null && textItems.size() > 0) {
            for (int i = 0; i < textItems.size(); i++) {
                TextView textView = textItems.get(i);
                textView.setTextSize(size);
            }
        }
        PrefUtil.Instance().setZoomPageTextSize(size);
    }

    /**
     * Returns the distance between two pointers on the screen.
     */
    private int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    public void toSmallTextSize() {
        float currentTextSize = PrefUtil.Instance().getZoomPageTextSize();
        currentTextSize -= TEXT_SIZE_DISTANCE;

        if (currentTextSize < MIN_TEXT_SIZE)
            currentTextSize = MIN_TEXT_SIZE;
        PrefUtil.Instance().setZoomPageTextSize(currentTextSize);
        ratio = currentTextSize - 9;
        PrefUtil.Instance().setZoomPageTextRatio(ratio);
        if (textItems != null && textItems.size() > 0) {
            for (int i = 0; i < textItems.size(); i++) {
                TextView textView = textItems.get(i);
                textView.setTextSize(currentTextSize);
            }
        }
    }

    public void toBigTextSize() {
        float currentTextSize = PrefUtil.Instance().getZoomPageTextSize();
        currentTextSize += TEXT_SIZE_DISTANCE;

        if (currentTextSize > MAX_TEXT_SIZE)
            currentTextSize = MAX_TEXT_SIZE;
        ratio = currentTextSize - 9;
        PrefUtil.Instance().setZoomPageTextRatio(ratio);
        PrefUtil.Instance().setZoomPageTextSize(currentTextSize);
        if (textItems != null && textItems.size() > 0) {
            for (int i = 0; i < textItems.size(); i++) {
                TextView textView = textItems.get(i);
                textView.setTextSize(currentTextSize);
            }
        }
    }

    /**
     * Sets the enabled state of the zoom feature.
     */
    public void setZoomEnabled(boolean enabled) {
        this.zoomEnabled = enabled;
    }

    /**
     * Returns the enabled state of the zoom feature.
     */
    public boolean isZoomEnabled() {
        return zoomEnabled;
    }

    /**
     * get text from textItems
     */
    public List<String> getTextsFromItems() {
        List<String> texts = new ArrayList<>();
        if (textItems != null && !textItems.isEmpty()) {
            for (TextView textView : textItems) {
                texts.add(textView.getText().toString());
            }
        }
        return texts;
    }

    public List<TextView> getTextViewItems() {
        return textItems;
    }

    public List<View> getAllViews() {
        return allViews;
    }

    public void setImageReferer(String referer) {
        this.referer = referer;
    }

    /**
     * get urls from textItems
     */
    public List<String> getImageUrlsFromItems() {
        return imagesUrls;
    }

    public void setOnPageItemClickListener(OnPageItemClickListener clickListener) {
        this.onPageItemClickListener = clickListener;
    }

    public interface OnPageItemClickListener {

        void onTextClick(TextView textView, int textPosition);

        void onImageClick(ImageView imageView, int imagePosition, List<String> allImageUrls);

        void onViewClick(View view);
    }

    @Override
    public void onClick(View view) {
        if (onPageItemClickListener != null) {
            if (view instanceof TextView) {
                onPageItemClickListener.onTextClick((TextView) view, (Integer) view.getTag(R.id.item_text));
            } else if (view instanceof ImageView) {
                onPageItemClickListener.onImageClick((ImageView) view, (Integer) view.getTag(R.id.item_image), imagesUrls);
            } else
                onPageItemClickListener.onViewClick(view);
        }
    }

    public void setOutsideLayout(OutsideDownFrameLayout bodyLayout) {
        mBodyLayout = bodyLayout;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (null != mChangedListener && 0 != oldw && 0 != oldh) {
            if (oldh - h > 300) {
                mShowKeyboard = true;
                mChangedListener.onChanged(mShowKeyboard);
            } else if (h - oldh > 300) {
                mShowKeyboard = false;
                mChangedListener.onChanged(mShowKeyboard);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (!listeners.isEmpty()) {
            for (OnScrollChangedListener listener : listeners)
                listener.onScrollChanged(l, t, oldl, oldt);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mChangedListener = listener;
    }

    /**
     * 增加监听器
     *
     * @param onScrollChangedListener
     */
    public void addScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        listeners.add(onScrollChangedListener);
    }

    /**
     * 移除不用的滑动监听器
     *
     * @param onScrollChangedListener
     */
    public void removeScrollListener(
            OnScrollChangedListener onScrollChangedListener) {
        if (listeners.contains(onScrollChangedListener)) {
            listeners.remove(onScrollChangedListener);
        }
    }


    public interface OnSizeChangedListener {
        void onChanged(boolean showKeyboard);
    }

    /**
     * 移动滑动监听器
     */
    public void removeOnScrollChangedListener(OnScrollChangedListener listener) {
        removeScrollListener(listener);
    }
}
