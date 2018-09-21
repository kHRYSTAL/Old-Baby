package com.oldbaby.common.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.oldbaby.R;
import com.oldbaby.oblib.util.DensityUtil;

/**
 * usage: 标题文字按钮快速创建
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public class TitleCreator {
    private static TitleCreator instance;

    public static TitleCreator Instance() {
        if (instance == null) {
            synchronized (TitleCreator.class) {
                if (instance == null) {
                    instance = new TitleCreator();
                }
            }
        }
        return instance;
    }

    private TitleCreator() {}

    /**
     * 创建一个标题上的默认颜色的TextView
     */
    public TextView createTextButton(Context context, String text) {
        return createTextButton(context, text, -1);
    }

    /**
     * 创建一个标题上的带颜色的TextView
     */
    public TextView createTextButton(Context context, String text, int color) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        if (color != -1) {
            textView.setTextColor(context.getResources().getColor(color));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.color_f2));
        }
        DensityUtil.setTextSize(textView, R.dimen.app_title_btn_text_size);
        return textView;
    }

    /**
     * 创建一个标题上的带颜色的TextView
     */
    public TextView createTextButton(Context context, String text, ColorStateList color) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setTextColor(color);
        DensityUtil.setTextSize(textView, R.dimen.app_title_btn_text_size);
        return textView;
    }

    /**
     * 创建一个标题上的ImageView
     */
    public ImageView createImageButton(Context context, int resId) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        return imageView;
    }
}
