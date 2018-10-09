package com.oldbaby.oblib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.MLog;

@SuppressLint("AppCompatCustomView")
public class ImageViewEx extends ImageView {
    private static final String TAG = "MulitPointTouchImageView";
    private Bitmap bm;

    // This is the base transformation which is used to show the image
    // initially. The current computation for this shows the image in
    // it's entirety, letterboxing as needed. One could choose to
    // show the image as cropped instead.
    //
    // This matrix is recomputed when we go from the thumbnail image to
    // the full size image.
    protected Matrix mBaseMatrix = new Matrix();

    // This is the supplementary transformation which reflects what
    // the user has done in terms of zooming and panning.
    //
    // This matrix reMains the same when we go from the thumbnail
    // image
    // to the full size image.
    protected Matrix mSuppMatrix = new Matrix();

    // This is the final matrix which is computed as the concatentation
    // of the base matrix and the supplementary matrix.
    private final Matrix mDisplayMatrix = new Matrix();

    // Temporary buffer used for getting the values out of a matrix.
    private final float[] mMatrixValues = new float[9];

    // The current bitmap being displayed.
    // protected final RotateBitmap mBitmapDisplayed = new RotateBitmap(null);
    protected Bitmap mBitmap = null;

    int mThisWidth = -1, mThisHeight = -1;

    float mMaxZoom = 2.0f;// 最大缩放比例
    float mMinZoom;// 最小缩放比例

    private int mBitmapOriginalWidth;// 图片的原始宽度
    private int mBitmapOriginalHeight;// 图片的原始高度

    private float mFitScreenScale = 1.0f;// 图片适应屏幕的缩放比例
    public boolean mIsScaleFinish = false;
    public boolean mIsTranslateFinish = false;

    private int mScreenWidth;
    private int mScreenHeight;
    private boolean isPlaceHolder = false;

    public ImageViewEx(Context context, int imageWidth, int imageHeight) {
        super(context);
        this.mBitmapOriginalHeight = imageHeight;
        this.mBitmapOriginalWidth = imageWidth;
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        init();
    }

    public ImageViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        init();
    }

    public ImageViewEx(Context context, AttributeSet attrs, int imageWidth,
                       int imageHeight) {
        super(context, attrs);
        this.mBitmapOriginalHeight = imageHeight;
        this.mBitmapOriginalWidth = imageWidth;
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        init();
    }

    public void initImageView(Context context, int imageWidth, int imageHeight) {
        this.mBitmapOriginalHeight = imageHeight;
        this.mBitmapOriginalWidth = imageWidth;
        mScreenWidth = DensityUtil.getWidth();
        mScreenHeight = DensityUtil.getHeight();
        init();
    }

    public Bitmap getImageBitmap() {
        return this.bm;
    }

    public void setImageWidthAndHeight(int imageWidth, int imageHeight) {
        this.mBitmapOriginalHeight = imageHeight;
        this.mBitmapOriginalWidth = imageWidth;

        init();
    }

    // /**
    // * 计算图片正好适配屏幕的缩放比例
    // */
    // private void arithmeticFitScreenScale() {
    // if (mBitmapOriginalWidth > mScreenWidth
    // || mBitmapOriginalHeight > mScreenHeight) {
    // if (mBitmapOriginalWidth <= mScreenWidth) {
    // float fitScreenScaleWidth = mScreenWidth
    // / (float) mBitmapOriginalWidth;
    // mFitScreenScale = fitScreenScaleWidth;
    // } else if (mBitmapOriginalHeight <= mScreenHeight) {
    // float fitScreenScaleHeight = mScreenHeight
    // / (float) mBitmapOriginalHeight;
    // mFitScreenScale = fitScreenScaleHeight;
    // } else {
    // float fitScreenWidthScale = mScreenWidth
    // / (float) mBitmapOriginalWidth;
    // float fitScreenHeight = fitScreenWidthScale
    // * (float) mBitmapOriginalHeight;
    // if (fitScreenHeight > mScreenHeight) {
    // float fitScreenHeightScale = mScreenHeight
    // / (float) mBitmapOriginalHeight;
    // mFitScreenScale = fitScreenHeightScale;
    // } else {
    // mFitScreenScale = fitScreenWidthScale;
    // }
    // }
    // } else {
    // float fitScreenWidthScale = mScreenWidth
    // / (float) mBitmapOriginalWidth;
    // float fitScreenHeight = fitScreenWidthScale
    // * (float) mBitmapOriginalHeight;
    // if (fitScreenHeight > mScreenHeight) {
    // float fitScreenHeightScale = mScreenHeight
    // / (float) mBitmapOriginalHeight;
    // mFitScreenScale = fitScreenHeightScale;
    // } else {
    // mFitScreenScale = fitScreenWidthScale;
    // }
    // }
    // }

    /**
     * 计算图片正好适配屏幕的缩放比例
     */
    private void arithmeticFitScreenScale() {
        if (mBitmapOriginalWidth > mScreenWidth
                || mBitmapOriginalHeight > mScreenHeight) {
            if (mBitmapOriginalWidth <= mScreenWidth) {
                // float fitScreenScaleHeight = (float) mBitmapOriginalHeight
                // / mScreenHeight;
                float fitScreenScaleHeight = mScreenWidth
                        / (float) mBitmapOriginalWidth;
                mFitScreenScale = fitScreenScaleHeight;
            } else if (mBitmapOriginalHeight <= mScreenHeight) {
                float fitScreenScaleWidth = mScreenWidth
                        / (float) mBitmapOriginalWidth;
                mFitScreenScale = fitScreenScaleWidth;
            } else {
                float fitScreenWidthScale = mScreenWidth
                        / (float) mBitmapOriginalWidth;
                float fitScreenHeight = fitScreenWidthScale
                        * (float) mBitmapOriginalHeight;
                if (fitScreenHeight > mScreenHeight) {
                    float fitScreenHeightScale = mScreenHeight
                            / (float) mBitmapOriginalHeight;
                    mFitScreenScale = fitScreenHeightScale;
                } else {
                    mFitScreenScale = fitScreenWidthScale;
                }
            }
        } else {
            float fitScreenWidthScale = mScreenWidth
                    / (float) mBitmapOriginalWidth;
            float fitScreenHeight = fitScreenWidthScale
                    * (float) mBitmapOriginalHeight;
            if (fitScreenHeight > mScreenHeight) {
                // float fitScreenHeightScale = mScreenHeight
                // / (float) mBitmapOriginalHeight;
                // mFitScreenScale = fitScreenHeightScale;
                mFitScreenScale = fitScreenWidthScale;
            } else {
                mFitScreenScale = fitScreenWidthScale;
            }
        }
    }

    /**
     * 获取正好适配屏幕时的缩放比例
     *
     * @return
     */
    public float getFitScreenScale() {
        return mFitScreenScale;
    }

    /**
     * 获得图片的原始尺寸的宽
     *
     * @return
     */
    public int getBitmapOriginalWidth() {
        return mBitmapOriginalWidth;
    }

    public void setBitmapOriginalWidth(int bitmapOriginalWidth) {
        this.mBitmapOriginalWidth = bitmapOriginalWidth;
    }

    /**
     * 获得图片的原始尺寸的高
     *
     * @return
     */
    public int getBitmapOriginalHeight() {
        return mBitmapOriginalHeight;
    }

    public void setBitmapOriginalHeight(int bitmapOriginalHeight) {
        this.mBitmapOriginalHeight = bitmapOriginalHeight;
    }

    public float getDisplayWidth() {
        return mBitmapOriginalWidth * mFitScreenScale;
    }

    public float getDisplayHeight() {
        return mBitmapOriginalHeight * mFitScreenScale;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            if (getScale() > 1.0f) {
                // If we're zoomed in, pressing Back jumps out to show the
                // entire image, otherwise Back returns the user to the gallery.
                zoomTo(1.0f);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    protected Handler mHandler = new Handler();

//	@Override
//	public void setImageBitmap(Bitmap bitmap) {
//		super.setImageBitmap(bitmap);
//		this.bm = bitmap;
//		if (bitmap == null)
//			return;
//
//		this.mBitmapOriginalHeight = bitmap.getHeight();
//		this.mBitmapOriginalWidth = bitmap.getWidth();
//		mBitmap = bitmap;
//		// 计算适应屏幕的比例
//		// arithmeticFitScreenScale();
//		arithmeticFitScreenScale();
//
//		MLog.d(TAG, "setImageBitmap() mFitScreenScale:" + mFitScreenScale);
//		// 在这里要过滤一下,当图片的宽和高都小于屏幕,这时图片不需要进行缩放,直接显示出来即可;只有当图片的宽或高大于屏幕时,才进行缩放
//		if (mBitmapOriginalWidth > mScreenWidth
//				|| mBitmapOriginalHeight > mScreenHeight) {
//			// 缩放到屏幕大小
//			zoomTo(mFitScreenScale, mScreenWidth / 2f, mScreenHeight / 2f);
//		} else {
////			zoomTo(getFitScreenScale(), mScreenWidth / 2, mScreenHeight / 2,
////					200f);
//			zoomTo(getFitScreenScale(), mScreenWidth / 2f, mScreenHeight / 2f);
//			
//		}
//		MLog.d(TAG, "setImageBitmap() mBitmapOriginalHeight:"
//				+ mBitmapOriginalHeight + " mBitmapOriginalWidth:"
//				+ mBitmapOriginalWidth);
//		// 居中
//
//		layoutToCenter();
//		// center(true, true);
//
//	}

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (drawable == null)
            return;
        this.bm = ((BitmapDrawable) drawable).getBitmap();
        if (this.bm == null)
            return;

        this.mBitmapOriginalHeight = this.bm.getHeight();
        this.mBitmapOriginalWidth = this.bm.getWidth();
        mBitmap = this.bm;
        // 计算适应屏幕的比例
        // arithmeticFitScreenScale();
        arithmeticFitScreenScale();

        MLog.d(TAG, "setImageBitmap() mFitScreenScale:" + mFitScreenScale);
        // 在这里要过滤一下,当图片的宽和高都小于屏幕,这时图片不需要进行缩放,直接显示出来即可;只有当图片的宽或高大于屏幕时,才进行缩放
        if (mBitmapOriginalWidth > mScreenWidth
                || mBitmapOriginalHeight > mScreenHeight) {
            // 缩放到屏幕大小
            zoomTo(mFitScreenScale, 0, 0);
        } else {
            // zoomTo(getFitScreenScale(), mScreenWidth / 2, mScreenHeight / 2,
            // 200f);
            zoomTo(getFitScreenScale(), 0, 0);
            //zoomTo(getFitScreenScale(), mScreenWidth / 2, 0);
        }
        MLog.d(TAG, "setImageBitmap() mBitmapOriginalHeight:"
                + mBitmapOriginalHeight + " mBitmapOriginalWidth:"
                + mBitmapOriginalWidth);
        // 居中

        layoutToCenter();

    }

    // Center as much as possible in one or both axis. Centering is
    // defined as follows: if the image is scaled down below the
    // view's dimensions then center it (literally). If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    protected void center(boolean horizontal, boolean vertical) {
        // if (mBitmapDisplayed.getBitmap() == null) {
        // return;
        // }
        if (mBitmap == null) {
            return;

        }

        Matrix m = getImageViewMatrix();

        // 屏幕的右下角为原点
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        MLog.d(TAG, "Bitmap getWidth():" + mBitmap.getWidth()
                + " Bitmap getHeight()" + mBitmap.getHeight());

        // mapRect()指的是获取当前屏幕上图片能显示的最大的长和宽,同时把这个长和宽赋值给这个矩形
        // 首先得到图片的宽为width,高为height,你使用的matrix为m:
        //
        // RectF rect = new RectF(0, 0, width, height);
        // m.mapRect(rect);
        //
        // 这样rect.left，rect.right,rect.top,rect.bottom分别就就是当前屏幕离你的图片的边界的距离。
        // 而这样你要的点（0，0）其实就是界面的左上角（rect.left，rect.top）
        // 可能会有正负，你写了之后可以看一下
        m.mapRect(rect);

        // 得到矩形的高度
        float height = rect.height();
        // 得到矩形的宽度
        float width = rect.width();
        MLog.d(TAG, "rect width:" + width + " rect height" + height);
        float deltaX = 0, deltaY = 0;

        // 打印当前屏幕上显示的图片的坐标,注意,此时的坐标不是基于屏幕左上角的0,0坐标,而此时的0,0坐标是以该图片的原始尺寸为标准的,所以坐标数才会出现几百甚至上千
        // 而此时打印出的top,left才是屏幕左上角0,0坐标的位置所在,根据这个top,left就可以算出屏幕的居中关系
        MLog.d(TAG, "rect top:" + rect.top + " rect bottom:" + rect.bottom
                + " rect left:" + rect.left + " rect right:" + rect.right);
        MLog.d(TAG, "imageview Width:" + getWidth() + " imageview Height"
                + getHeight());

        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
            float viewHeight = getHeight();
            if (height < viewHeight) {
                // 矩形的偏移量
                deltaY = (viewHeight - height) / 2 - rect.top;
                // MLog.d(TAG, "vertical rect height < viewHeight deltaY:" +
                // deltaY
                // + " rect.top:" + rect.top);
            } else if (rect.top > 0) {
                deltaY = -rect.top;
                // MLog.d(TAG, "vertical rect.top > 0 deltaY:" + deltaY
                // + " rect.top:" + rect.top);
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
                // MLog.d(TAG, "vertical rect.bottom < viewHeight > 0 deltaY:"
                // + deltaY + " rect.bottom:" + rect.bottom);
            }
        }

        if (horizontal) {
            float viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
                // MLog.d(TAG, "horizontal width < viewWidth deltaX:" + deltaX
                // + " rect.left:" + rect.left);
            } else if (rect.left > 0) {
                deltaX = -rect.left;
                // MLog.d(TAG, "horizontal rect.left > 0 deltaX:" + deltaX
                // + " rect.left:" + rect.left);
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
                // MLog.d(TAG, "horizontal width < viewWidth deltaX:" + deltaX
                // + " rect.right:" + rect.right);
            }
        }

        // 移动图片
        postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }

    private void init() {
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    /**
     * 设置图片居中显示
     */
    public void layoutToCenter() {
        // 正在显示的图片实际宽高
        float width = mBitmapOriginalWidth * getScale();
        float height = mBitmapOriginalHeight * getScale();

        // 空白区域宽高
        float fill_width = mScreenWidth - width;
        float fill_height = mScreenHeight - height;

        // 需要移动的距离
        float tran_width = 0f;
        float tran_height = 0f;

        float v[] = new float[9];
        Matrix m = getImageMatrix();
        m.getValues(v);
        // 图片实时的上下左右坐标
        float left, top;

        left = v[Matrix.MTRANS_X];
        top = v[Matrix.MTRANS_Y];

        if (fill_width > 0f) {
            tran_width = fill_width / 2f - left;
        }
        if (fill_height > 0f) {
            tran_height = fill_height / 2f - top;
        }

        postTranslate(tran_width, tran_height);
        setImageMatrix(getImageViewMatrix());
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        mMinZoom = (mScreenWidth / 2f) / mBitmapOriginalWidth;

        return mMatrixValues[whichValue];
    }

    // Get the scale factor out of the matrix.
    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    /**
     * 获得图片当前的缩放比例
     *
     * @return
     */
    public float getScale() {
        return getScale(mSuppMatrix);
    }

    // Combine the base matrix and the supp matrix to make the final matrix.
    protected Matrix getImageViewMatrix() {
        // The final matrix is computed as the concatentation of the base matrix
        // and the supplementary matrix.
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    static final float SCALE_RATE = 1.25F;

    // Sets the maximum zoom, which is a scale relative to the base matrix. It
    // is calculated to show the image at 400% zoom regardless of screen or
    // image orientation. If in the future we decode the full 3 megapixel image,
    // rather than the current 1024x768, this should be changed down to 200%.
    protected float maxZoom() {
        if (mBitmap == null) {
            return 1F;
        }

        float fw = (float) mBitmap.getWidth() / (float) mThisWidth;
        float fh = (float) mBitmap.getHeight() / (float) mThisHeight;
        float max = Math.max(fw, fh) * 4;
        return max;
    }

    public void zoomTo(float scale, float centerX, float centerY) {
        // 只有屏幕装不下图片时,才需要限制缩放的比例,对于屏幕能放下的图片,则不需要过滤;默认所有图片的放大比例是1.0
        if (getBitmapOriginalWidth() > mScreenWidth
                && getBitmapOriginalHeight() > mScreenHeight) {
            if (scale > mMaxZoom) {
                scale = mMaxZoom;
            } else if (scale < mMinZoom) {
                scale = mMinZoom;
            }
        }
        float oldScale = getScale();// 得到原始放大比例
        float deltaScale = scale / oldScale;// 需要放大到原来的几倍

        //mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    /**
     * 将图片在durationMs秒内缩放到newScale数值的比例
     *
     * @param newScale   缩放到的比例
     * @param centerX    图片的中心坐标X
     * @param centerY    图片的中心坐标Y
     * @param durationMs 缩放的所用的时间
     */
    public void zoomTo(final float newScale, final float centerX,
                       final float centerY, final float durationMs) {

        mIsScaleFinish = false;

        final float oldScale = getScale();
        // MLog.d(TAG, "zoomTo() durationMs:" + durationMs);
        // MLog.d(TAG, "zoomTo() newScale:" + newScale);
        // MLog.d(TAG, "zoomTo() oldScale:" + oldScale);
        // 得到每一毫秒放大或缩小,变化的平均量
        final float incrementPerMs = (newScale - oldScale) / durationMs;
        // MLog.d(TAG, "zoomTo() incrementPerMs:" + incrementPerMs);

        // 记录执行的开始时间
        final long startTime = System.currentTimeMillis();

        // MLog.d(TAG, "zoomTo() startTime:" + startTime);
        mHandler.post(new Runnable() {
            public void run() {
                // 得到每次缩放的起始时间
                long runnableStartTime = System.currentTimeMillis();
                // MLog.d(TAG, "zoomTo() Runnable runnableStartTime:"
                // + runnableStartTime);
                // 获得
                // 规定的缩放时间和实际每次执行的缩放时间之间最小的一个值,如果实际的执行时间大于规定的时间,则认为缩放时间已到,应停止缩放
                float currentMs = Math.min(durationMs, runnableStartTime
                        - startTime);
                // MLog.d(TAG, "zoomTo() Runnable currentMs:" + currentMs);

                // 得到本次要缩放的比例,(incrementPerMs *
                // currentMs)表示从第一次开始一直到此时间段内,需要缩放的比例;oldScale表示再加上上一次缩放的比例,
                // 就能得出到当前时间点位置,需要缩放的最终比例,一次类推,每一次都计算一个总比例,这样就会有逐渐放大的效果,而不是一下放大到目标比例
                float target = oldScale + (incrementPerMs * currentMs);
                // MLog.d(TAG, "zoomTo() Runnable target:" + target);

                // 做缩放
                zoomTo(target, centerX, centerY);

                // 如果缩放逐渐缩放这个动作话费的时间已经超过规定的毫秒数,则认为已经缩放完毕,停止线程;如果小于规定的时间,则认为还没有缩放到目标比例,需要循环进行,再次掉用本线程
                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    mIsScaleFinish = true;
                }
            }
        });
    }

    public void zoomTo(float scale) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy);
    }

    protected void zoomOut(float rate) {
        if (mBitmap == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        // Zoom out to at most 1x.
        Matrix tmp = new Matrix(mSuppMatrix);
        tmp.postScale(1F / rate, 1F / rate, cx, cy);

        if (getScale(tmp) < 1F) {
            mSuppMatrix.setScale(1F, 1F, cx, cy);
        } else {
            mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
        }
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }

    public void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    float _dy = 0.0f;

    public void postTranslateDur(final float dy, final float durationMs) {
        mIsTranslateFinish = false;
        _dy = 0.0f;
        final float incrementPerMs = dy / durationMs;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);

                postTranslate(0, incrementPerMs * currentMs - _dy);
                _dy = incrementPerMs * currentMs;

                if (currentMs < durationMs) {
                    mHandler.post(this);
                } else {
                    mIsTranslateFinish = true;
                }
            }
        });
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        super.setFrame(l, t, r, b);
        return true;
    }

    public boolean isScaleFinished() {
        return mIsScaleFinish;
    }

    public void setIsPlaceHolder(boolean b) {
        isPlaceHolder = b;
    }

    public boolean isPlaceHolder() {
        return isPlaceHolder;
    }

    // protected void panBy(float dx, float dy) {
    // postTranslate(dx, dy);
    // setImageMatrix(getImageViewMatrix());
    // }
}