package com.oldbaby.common.view.zoompage;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oldbaby.R;
import com.oldbaby.common.app.PrefUtil;
import com.oldbaby.common.bean.PageItem;
import com.oldbaby.oblib.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * usage: 支持容器内所有文字控件放大的 NestedScrollView 未来文章详情页支持评论后 需要把父类改为linearlayout
 *  然后作为recyclerview的header
 * author: kHRYSTAL
 * create time: 18/9/28
 * update time:
 * email: 723526676@qq.com
 */
public class PinchZoomPage extends NestedScrollView implements View.OnClickListener {

    //Consider each "step" between the two pointers as 200 px. In other words, the TV size will grow every 200 pixels.
    private static final float STEP = 200;

    // The ratio of the text size compared to its original.
    private float ratio = 1.0f;

    // The distance between the two pointers when they are first placed on the screen.
    private int baseDistance;

    // The ratio of the text size when the gesture is started.
    private float baseRatio;

    // Boolean flag for whether or not zoom feature is enabled. Defaults to true.
    private boolean zoomEnabled = true;


    // real container
    private LinearLayout container;
    private List<TextView> textItems;
    private List<ImageView> imageItems;
    private List<String> imagesUrls;

    private OnPageItemClickListener onPageItemClickListener;

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

    private void initView(Context context) {
        // init container
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        container.setLayoutParams(layoutParams);
        addView(container);
        textItems = new ArrayList<>();
        imageItems = new ArrayList<>();
        imagesUrls = new ArrayList<>();
        ratio = PrefUtil.Instance().getZoomPageTextRatio();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
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
            int action = ev.getAction();
            int distance = getDistance(ev);
            int pureAction = action & MotionEvent.ACTION_MASK;
            if (pureAction == MotionEvent.ACTION_POINTER_DOWN) {
                baseDistance = distance;
                baseRatio = ratio;
            } else {
                float delta = (distance - baseDistance) / STEP;
                float multi = (float) Math.pow(2, delta);
                ratio = Math.min(1024.0f, Math.max(0.1f, baseRatio * multi));
                setTextSize(ratio + 13);
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public void setContent(List<PageItem> pageItems) {
        container.removeAllViews();
        textItems.clear();
        imageItems.clear();
        imagesUrls.clear();
        if (pageItems == null || pageItems.isEmpty())
            return;
        scrollTo(0, 0);
        float defaultTextSize = PrefUtil.Instance().getZoomPageTextSize(getContext());
        for (int i = 0; i < pageItems.size(); i++) {
            PageItem pi = pageItems.get(i);
            if (StringUtil.isEquals(pi.type, PageItem.TYPE_TEXT)) {
                TextView textView = new TextView(getContext());
                textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText(pi.text);
                textView.setTextSize(defaultTextSize);
                container.addView(textView);
                textItems.add(textView);
                // 设置textView 所在的文字列表顺序
                textView.setTag(R.id.item_text, textItems.size() - 1);
                // TODO: 18/9/30 需要重写textview 判断手势 否则点击事件会将父容器手势消费
//                textView.setOnClickListener(this);
            } else if (StringUtil.isEquals(pi.type, PageItem.TYPE_TEXT)) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container.addView(imageView);
                Glide.with(getContext()).load(pi.imageUrl).into(imageView);
                imageItems.add(imageView);
                imagesUrls.add(pi.imageUrl);
                // 设置imageView 所在的图片列表顺序
                imageView.setTag(R.id.item_image, imageItems.size() - 1);
//                imageView.setOnClickListener(this);
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
        if (textItems != null && textItems.size() > 0) {
            for (int i = 0; i < textItems.size(); i++) {
                TextView textView = textItems.get(i);
                textView.setTextSize(size);
            }
        }
        PrefUtil.Instance().setZoomPageTextRatio(ratio);
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
            if (view instanceof TextView)
                onPageItemClickListener.onTextClick((TextView) view, (Integer) view.getTag(R.id.item_text));
            else if (view instanceof ImageView)
                onPageItemClickListener.onImageClick((ImageView) view, (Integer) view.getTag(R.id.item_image), imagesUrls);
            else
                onPageItemClickListener.onViewClick(view);
        }
    }
}
