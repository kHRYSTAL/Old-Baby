package com.oldbaby.common.base;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oldbaby.R;
import com.oldbaby.oblib.component.act.TitleType;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.view.title.OnTitleClickListener;
import com.oldbaby.oblib.view.title.TitleBar;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/21
 * update time:
 * email: 723526676@qq.com
 */
public class TitleBarProxy {

    public static final int TAG_BACK = 601;
    private TitleBar titleBar;

    public void configTitle(View root, int titleType, OnTitleClickListener titleClickListener) {
        switch (titleType) {
            case TitleType.TITLE_LAYOUT: {
                View view = root.findViewById(R.id.custom_title);
                if (view == null) {
                    throw new UnsupportedOperationException();
                }
                titleBar = new TitleBar(root, titleClickListener);
                titleBar.setBackgroud(R.color.white);
                titleBar.setTextStyle(R.style.title_bar);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 添加返回button
     */
    public void addBackButton() {
        if (titleBar != null) {
            View backBtn = TitleCreator.Instance().createImageButton(
                    OGApplication.APP_CONTEXT, R.drawable.sel_btn_back);
            titleBar.addLeftTitleButton(backBtn, TAG_BACK);
        }
    }

    /**
     * 添加返回button
     *
     * @param btnBackResID
     */
    public void addBackButton(int btnBackResID) {
        if (titleBar != null) {
            View backBtn = TitleCreator.Instance().createImageButton(OGApplication.APP_CONTEXT, btnBackResID);
            titleBar.addLeftTitleButton(backBtn, TAG_BACK);
        }
    }

    /**
     * 添加标题
     */
    public void setTitle(String title) {
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    /**
     * 添加标题
     *
     * @param title
     */
    public void setTitle(SpannableString title) {
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    /**
     * 设置标题字体样式
     */
    public void setTitleTypeFace(Typeface typeFace) {
        if (titleBar != null) {
            titleBar.setTitleTypeFace(typeFace);
        }
    }

    /**
     * 添加标题
     */
    public void setImageTitle(int resId) {
        if (titleBar != null) {
            titleBar.setImageTitle(resId);
        }
    }

    /**
     * 添加背景
     */
    public void setTitleBackground(int resId) {
        if (titleBar != null) {
            titleBar.setBackgroud(resId);
        }
    }


    /**
     * 获取标题
     */
    public void getTitle() {
        if (titleBar != null) {
            titleBar.getTitle();
        }
    }

    /**
     * 获取TextView控件
     */
    public TextView getTitleTextView() {
        if (titleBar != null) {
            return titleBar.getTitleTextView();
        }
        return null;
    }

    /**
     * 添加titlebar左边button
     */
    public void addLeftTitleButton(View view, int tagId) {
        if (titleBar != null) {
            titleBar.addLeftTitleButton(view, tagId);
        }
    }

    /**
     * 添加titlebar左边button 可配置layoutparams
     */
    public void addLeftTitleButton(View view, int tagId, LinearLayout.LayoutParams params) {
        if (titleBar != null) {
            titleBar.addLeftTitleButton(view, tagId, params);
        }
    }

    /**
     * 添加titlebar右边button
     */
    public void addRightTitleButton(View view, int tagId) {
        if (titleBar != null) {
            titleBar.addRightTitleButton(view, tagId);
        }
    }

    /**
     * 添加titlebar右边button 可配置layoutparams
     */
    public void addRightTitleButton(View view, int tagId, LinearLayout.LayoutParams params) {
        if (titleBar != null) {
            titleBar.addRightTitleButton(view, tagId, params);
        }
    }

    /**
     * 隐藏指定id的button
     * @param tagId
     */
    public void hideTitleButton(int tagId) {
        if (titleBar != null) {
            titleBar.hideTitleButton(tagId);
        }
    }

    public void hideAllButtons() {
        if (titleBar != null) {
            titleBar.hideAllButtons();
        }
    }

    public void hideRightButtons() {
        titleBar.hideRightButtons();
    }

    public void removeRightButtons() {titleBar.removeRightButtons();}

    /**
     * 显示指定id的button
     */
    public void showTitleButton(int tagId) {
        if (titleBar != null) {
            titleBar.showTitleButton(tagId);
        }
    }

    /**
     * enable指定id的button
     */
    public void enableTitleButton(int tagId) {
        if (titleBar != null) {
            titleBar.enableTitleButton(tagId);
        }
    }

    /**
     * disable指定id的button
     */
    public void disableTitleButton(int tagId) {
        if (titleBar != null) {
            titleBar.disableTitleButton(tagId);
        }
    }

    /**
     * 显示title下面的Line
     */
    public void showBottomLine() {
        if (titleBar != null) {
            titleBar.showBottomLine();
        }
    }

    /**
     * 隐藏title下面的Line
     */
    public void hideBottomLine() {
        if (titleBar != null) {
            titleBar.hideBottomLine();
        }
    }

    public View getRootView() {
        return titleBar.getRootView();
    }

    /**
     * 根据tagId来获取button
     *
     * @param tagId
     * @return
     */
    public View getButton(int tagId) {
        if (titleBar != null)
            return titleBar.getButton(tagId);
        return null;
    }

    /**
     * 设置标题颜色
     */
    public void setTitleColor(int titleColor) {
        titleBar.setTitleColor(titleColor);
    }


}
