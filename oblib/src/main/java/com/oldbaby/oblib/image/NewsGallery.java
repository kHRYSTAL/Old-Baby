package com.oldbaby.oblib.image;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.async.MyHandler;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.view.ImageViewEx;

public class NewsGallery extends Gallery implements MyHandler.HandlerListener {

    private static final int DOUBLE_TOUCH = 1;
    // private static final int DRAGING = 2;
    private static final int NONE = 3;
    private static final int CLICK = 4;

    private static final int UI_EVENT_SINGLETAP = 2001;
    private static final int SINGLETAP_DELAY = 200;

    private static final int MAX_MARGIN_TOP = 100;
    private static final int MAX_MARGIN_BOTTOM = 100;
    private static final int MARGIN_GAP = 20;

    private int touchMode = NONE;

    private final GestureDetector gestureScanner;
    private static final String TAG = "NewsGallery";
    private ImageViewEx mImageView;
    private final float mMaxScale = 3.0f;
    private final float mMinScale = 1.0f;
    private final float mMaxImageScale = 4.0f;
    private final float mMinImageScale = 0.5f;
    private float baseValue;
    float originalScale;
    private final int mScreenWidth;
    private final int mScreenHeight;

    // 缩放的比例 X Y方向都是这个值 越大缩放的越快
    private float currentScale = 0.06f;
    private float scaleCX;
    private float scaleCY;
    public GalleryListener listener = null;
    protected boolean isDoubleTap = false;

    private final Handler galleryHandler = new MyHandler(this);

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {

            case UI_EVENT_SINGLETAP:
                if (!isDoubleTap) {
                    if (listener != null)
                        listener.onSingleTabUp();
                }
                isDoubleTap = false;
                break;

            default:
                break;
        }
        return true;
    }

    public NewsGallery(Context context) {
        super(context);
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        gestureScanner = new GestureDetector(new TouchGesture());
    }

    public NewsGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        gestureScanner = new GestureDetector(new TouchGesture());
    }

    public NewsGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        gestureScanner = new GestureDetector(new TouchGesture());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (touchMode == DOUBLE_TOUCH) {
            return false;
        }

        View view = getGallerySelectedView();
        if (view == null)
            return false;
        if (view instanceof ImageViewEx) {

            mImageView = (ImageViewEx) view;

            float v[] = new float[9];
            Matrix m = mImageView.getImageMatrix();
            m.getValues(v);
            // 图片实时的上下左右坐标
            float left, right;

            // 图片的实时宽，高
            float width, height;

            width = mImageView.getScale() * mImageView.getBitmapOriginalWidth();
            height = mImageView.getScale()
                    * mImageView.getBitmapOriginalHeight();
            // 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！
            // 如果图片当前大小<屏幕大小，直接处理滑屏事件
            if ((int) width <= mScreenWidth && (int) height <= mScreenHeight) {
                super.onScroll(e1, e2, distanceX, distanceY);
            } else {
                left = v[Matrix.MTRANS_X];
                right = left + width;
                float top = v[Matrix.MTRANS_Y];
                float bottom = top + height;
                Rect r = new Rect();
                mImageView.getGlobalVisibleRect(r);

                // 判断左右边界是否到达图片边缘
                if (distanceX > 0)// 左滑
                {
                    if (r.left > 0) {
                        super.onScroll(e1, e2, distanceX, distanceY);
                    } else if (right < mScreenWidth)
                        super.onScroll(e1, e2, distanceX, distanceY);
                    else
                        mImageView.postTranslate(-distanceX, -0);
                }

                if (distanceX < 0)// 右滑
                {
                    if (r.right < mScreenWidth) {
                        super.onScroll(e1, e2, distanceX, distanceY);
                    } else if (left > 0)
                        super.onScroll(e1, e2, distanceX, distanceY);
                    else
                        mImageView.postTranslate(-distanceX, -0);
                }

                if (distanceY > 0) { // 上滑
                    if (bottom > mScreenHeight)
                        mImageView.postTranslate(-0, -distanceY);
                }

                if (distanceY < 0) { // 下滑
                    if (top < 0)
                        mImageView.postTranslate(-0, -distanceY);
                }
            }

        } else {
            super.onScroll(e1, e2, distanceX, distanceY);
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureScanner.onTouchEvent(event);

        MLog.d(TAG, "onTouchEvent()" + event.getAction());

        View view = getGallerySelectedView();
        if (view == null)
            return false;
        if (view instanceof ImageViewEx) {
            mImageView = (ImageViewEx) view;

            // if (mImageView.isPlaceHolder())
            // return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    touchMode = CLICK;
                    baseValue = 0;
                    originalScale = mImageView.getScale();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 2) {

                        touchMode = DOUBLE_TOUCH;
                        float x = event.getX(0) - event.getX(1);
                        float y = event.getY(0) - event.getY(1);

                        float value = (float) Math.sqrt(x * x + y * y);//
                        // 计算两点的距离
                        // System.out.println("value:" + value);
                        if (baseValue == 0) {
                            baseValue = value;
                        } else {

                            // 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                            float scale = value / baseValue;

                            // scale the image
                            currentScale = originalScale * scale;
                            MLog.d(TAG, "currentScale:" + currentScale);

                            // // 只有在允许的缩放范围内,才可以缩放
                            if (currentScale >= mImageView.getFitScreenScale()
                                    * mMinImageScale
                                    && currentScale <= mImageView
                                    .getFitScreenScale() * mMaxImageScale) {

                                scaleCX = x / 2 + event.getX(1);
                                scaleCY = y / 2 + event.getY(1);

                                mImageView.zoomTo(currentScale, scaleCX, scaleCY);

                            }

                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    if (touchMode == DOUBLE_TOUCH) {
                        float width = currentScale
                                * mImageView.getBitmapOriginalWidth();
                        float height = currentScale
                                * mImageView.getBitmapOriginalHeight();

                        if (width > mImageView.getDisplayWidth() * mMaxScale
                                || height > mImageView.getDisplayHeight()
                                * mMaxScale) {

                            originalScale = currentScale;
                            float scale = mImageView.getDisplayWidth() * mMaxScale
                                    / width;

                            currentScale = originalScale * scale;
                            mImageView.zoomTo(currentScale, scaleCX, scaleCY, 300f);
                        } else if (width < mImageView.getDisplayWidth() * mMinScale
                                || height < mImageView.getDisplayHeight()
                                * mMinScale) {

                            originalScale = currentScale;
                            float scale = mImageView.getDisplayWidth() * mMinScale
                                    / width;

                            currentScale = originalScale * scale;

                            mImageView.zoomTo(currentScale, scaleCX, scaleCY, 300f);
                        }

                    } else if (touchMode == CLICK) {
                        float width = mImageView.getScale()
                                * mImageView.getBitmapOriginalWidth();
                        float height = mImageView.getScale()
                                * mImageView.getBitmapOriginalHeight();

                        if ((int) width <= mScreenWidth
                                && (int) height <= mScreenHeight)// 如果图片当前大小<屏幕大小，判断边界
                        {
                            break;
                        }

                        float v[] = new float[9];
                        Matrix m = mImageView.getImageMatrix();
                        m.getValues(v);
                        float top = v[Matrix.MTRANS_Y];
                        float bottom = top + height;
                        if (top > 0 && bottom > mScreenHeight) {
                            mImageView.postTranslateDur(-top, 200f);
                        }
                        MLog.i("manga", "bottom:" + bottom);
                        if (bottom < mScreenHeight && top < 0) {
                            mImageView.postTranslateDur(mScreenHeight - bottom,
                                    200f);
                        }
                    }
                    touchMode = NONE;
                    break;
            }
        }
        return super.onTouchEvent(event);
        // return false;
    }

    private class TouchGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        // 按两下的第二下Touch down时触发
        public boolean onDoubleTap(MotionEvent e) {
            MLog.d(TAG, "onDoubleTap()");
            View view = getGallerySelectedView();
            if (view == null)
                return false;
            if (view instanceof ImageViewEx) {
                mImageView = (ImageViewEx) view;
                MLog.d(TAG,
                        "imageView.getScale:" + mImageView.getScale()
                                + " imageView.getFitScreenScale:"
                                + mImageView.getFitScreenScale());
                isDoubleTap = true;
                if (mImageView.getBitmapOriginalWidth() > mScreenWidth
                        || mImageView.getBitmapOriginalHeight() > mScreenHeight) {
                    // 当屏幕装不下当前图片
                    // imageView.getScale():图片当前的缩放比例;imageView.getScaleRate():图片在屏幕中正好显示的比例
                    if (mImageView.getScale() > mImageView.getFitScreenScale()) {
                        // 缩小
                        currentScale = mImageView.getFitScreenScale();
                        mImageView.zoomTo(currentScale, mScreenWidth / 2,
                                mScreenHeight / 2, 200f);
                    } else {
                        // 放大
                        currentScale = 1.0f;
                        mImageView.zoomTo(currentScale, mScreenWidth / 2,
                                mScreenHeight / 2, 200f);
                    }
                } else {
                    // 当屏幕正好能装下当前图片
                    // imageView.getScale():图片当前的缩放比例;imageView.getScaleRate():图片在屏幕中正好显示的比例
                    if (mImageView.getScale() < mMaxScale) {
                        // 放大
                        currentScale = mMaxScale;
                        mImageView.zoomTo(currentScale, mScreenWidth / 2,
                                mScreenHeight / 2, 200f);
                        // MLog.d(TAG, "放大");
                    } else {
                        // 缩小
                        currentScale = mImageView.getFitScreenScale();
                        mImageView.zoomTo(currentScale, mScreenWidth / 2,
                                mScreenHeight / 2, 200f);
                        // MLog.d(TAG, "缩小");
                    }
                }

            }
            // 此处将return true改为让父类去处理相关方法,而不是自己去处理
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Message msg = new Message();
            msg.what = UI_EVENT_SINGLETAP;
            galleryHandler.sendMessageDelayed(msg, SINGLETAP_DELAY);
            return super.onSingleTapUp(e);
        }
    }

    /**
     * 获得当前Gallery显示的视图
     *
     * @return
     */
    private View getGallerySelectedView() {
        View view = NewsGallery.this.getSelectedView();
        if (view == null)
            return null;
        ImageViewEx imageView = (ImageViewEx) view
                .findViewById(R.id.galleryimage);

        return imageView;
    }

    ;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int keyCode;
        if (isScrollingLeft(e1, e2)) {
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
            onKeyDown(keyCode, null);
        } else if (isScrollingRight(e1, e2)) {
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
            onKeyDown(keyCode, null);
        }

        // return true;
        return false;
    }

    private boolean isScrollingRight(MotionEvent e1, MotionEvent e2) {
        if (e1.getX() - e2.getX() > 100)
            return true;
        else
            return false;
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        // 调整左右滑动的灵敏度
        if (e2.getX() - e1.getX() > 100)
            return true;
        else
            return false;
    }

    /**
     * 把放大的图片恢复到原来的状态
     */
    public void recoveryImageMatrix() {
        View viewTemp = getGallerySelectedView();
        if (viewTemp == null) {
            return;
        }
        if (viewTemp instanceof ImageViewEx) {
            ImageViewEx mulitPointImageView = (ImageViewEx) viewTemp;
            // 缩小
            mulitPointImageView.zoomTo(mulitPointImageView.getFitScreenScale(),
                    mScreenWidth / 2, mScreenHeight / 2, 200f);
        }

    }

    public void setGestureListener(GalleryListener listener) {
        this.listener = listener;
    }
}