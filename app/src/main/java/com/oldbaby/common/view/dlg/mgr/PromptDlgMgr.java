package com.oldbaby.common.view.dlg.mgr;

import android.content.Context;
import android.view.View;

import com.oldbaby.common.view.dlg.PromptDialog;
import com.oldbaby.oblib.view.dialog.IPromptDlgMgr;
import com.oldbaby.oblib.view.dialog.PromptDlgAttr;
import com.oldbaby.oblib.view.dialog.PromptDlgListener;
import com.oldbaby.oblib.view.dialog.PromptDlgTwoBtnListener;

import java.util.HashMap;

/**
 * usage: 用来帮助一个component管理所有的进度对话框
 * author: kHRYSTAL
 * create time: 16/11/9
 * update time:
 * email: 723526676@qq.com
 */

public class PromptDlgMgr implements IPromptDlgMgr {

    HashMap<String, PromptDialog> dlgMap = new HashMap<>();

    @Override
    public void show(Context context, String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener) {
        show(context, tag, promptDlgAttr, listener, null);
    }

    @Override
    public void show(Context context, String tag, PromptDlgAttr promptDlgAttr, PromptDlgListener listener, PromptDlgTwoBtnListener twoBtnListener) {
        PromptDialog promptDialog;
        if (!dlgMap.containsKey(tag)) {
            promptDialog = new PromptDialog(context);
            promptDialog.setTag(tag);
            dlgMap.put(tag, promptDialog);
        } else {
            promptDialog = dlgMap.get(tag);
        }

        if (promptDlgAttr != null) {
            promptDialog.setImage(promptDlgAttr.iconUrl, promptDlgAttr.resId);
            promptDialog.setTitle(promptDlgAttr.title);
            promptDialog.setSubTitle(promptDlgAttr.subTitle);
            promptDialog.setCanceledOnTouchOutside(promptDlgAttr.canceledOnTouchOutside);
            promptDialog.setCancelable(promptDlgAttr.cancelable);
            promptDialog.showCloseView(promptDlgAttr.showClose);
            promptDialog.setDlgDesc(promptDlgAttr.underBtnText);

            if (promptDlgAttr.isTwoBtn) {
                promptDialog.getButton().setVisibility(View.GONE);
                promptDialog.getTwoBtnLayout().setVisibility(View.VISIBLE);
                promptDialog.setLeftBtn(promptDlgAttr.leftBtnText, promptDlgAttr.leftBtnBgResId, promptDlgAttr.leftBtnTextColorId);
                promptDialog.setRightBtn(promptDlgAttr.rightBtnText, promptDlgAttr.rightBtnBgResId, promptDlgAttr.rightBtnTextColorId);
                promptDialog.setTwoBtnListener(twoBtnListener);
            } else {
                promptDialog.getButton().setVisibility(View.VISIBLE);
                promptDialog.getTwoBtnLayout().setVisibility(View.GONE);
                promptDialog.setButtonText(promptDlgAttr.btnText);
                promptDialog.setButtonBgResource(promptDlgAttr.btnBgResId);
                promptDialog.setListener(listener);
            }

        }

        if (!promptDialog.isShowing()) {
            promptDialog.show();
        }
    }

    @Override
    public void hide(String tag) {
        if (dlgMap.containsKey(tag)) {
            PromptDialog dlg = dlgMap.get(tag);
            if (dlg.isShowing()) {
                dlg.dismiss();
            }
        }
    }

    public PromptDialog getPromptDialog(String tag) {
        return dlgMap.get(tag);
    }

    @Override
    public boolean isShowing(String tag) {
        if (dlgMap.containsKey(tag)) {
            PromptDialog dlg = dlgMap.get(tag);
            if (dlg.isShowing()) {
                return true;
            }
        }
        return false;
    }
}
