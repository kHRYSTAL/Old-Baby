package com.oldbaby.oblib.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.component.application.OGApplication;

public class ToastUtil {

    public static void showShort(String content) {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        LayoutInflater inflater = LayoutInflater
                .from(OGApplication.APP_CONTEXT);
        View view = inflater.inflate(R.layout.lay_toast_txt, null);
        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(content);
        Toast toast = new Toast(OGApplication.APP_CONTEXT);
        toast.setGravity(Gravity.BOTTOM, 0, DensityUtil.dip2px(70));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static void showLong(String content) {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }
        LayoutInflater inflater = LayoutInflater
                .from(OGApplication.APP_CONTEXT);
        View view = inflater.inflate(R.layout.lay_toast_txt, null);
        TextView tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvContent.setText(content);
        Toast toast = new Toast(OGApplication.APP_CONTEXT);
        toast.setGravity(Gravity.BOTTOM, 0, DensityUtil.dip2px(70));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}
