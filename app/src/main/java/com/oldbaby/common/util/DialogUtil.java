package com.oldbaby.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.oldbaby.R;
import com.oldbaby.common.app.PrefUtil;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/24
 * update time:
 * email: 723526676@qq.com
 */
public class DialogUtil {

    private static DialogUtil dialogUtil;
    private static Object sycObj = new Object();


    private DialogUtil() {
    }

    public static DialogUtil getInstatnce() {
        synchronized (sycObj) {
            if (dialogUtil == null) {
                dialogUtil = new DialogUtil();
            }
        }
        return dialogUtil;
    }


    /**
     * 选择发音人弹窗
     */
    public void showSpeechSelectDialog(final Context context) {
        new AlertDialog.Builder(context).setTitle("选择发音人")
                .setSingleChoiceItems(context.getResources().getStringArray(R.array.voicer_cloud_entries), // 单选框有几项,各是什么名字
                        PrefUtil.Instance().getSpeechPersonIndex(), // 默认的选项
                        new DialogInterface.OnClickListener() { // 点击单选框后的处理
                            public void onClick(DialogInterface dialog,
                                                int which) { // 点击了哪一项
                                String voicer = context.getResources().getStringArray(R.array.voicer_cloud_values)[which];
                                PrefUtil.Instance().setSpeechPersonName(voicer);
                                PrefUtil.Instance().setSpeechPersonIndex(which);
                                dialog.dismiss();
                            }
                        }).show();
    }
}
