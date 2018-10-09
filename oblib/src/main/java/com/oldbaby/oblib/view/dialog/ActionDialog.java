package com.oldbaby.oblib.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.oldbaby.oblib.R;
import com.oldbaby.oblib.component.adapter.BaseListAdapter;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;

import java.util.List;

public class ActionDialog extends Dialog {

    public static final int ID_CANCEL = -2;

    private String mTitle;
    private final String mCancelTitle;
    private List<ActionItem> mOtherBtnTitles;

    private TextView tvTitle;
    private TextView tvCancel;
    private int cancelTextColor;

    private Context context;

    public static interface OnActionClick {
        public void onClick(DialogInterface dialog, int id, ActionItem item);
    }

    private final OnActionClick mClickListener;

    public ActionDialog(Context context, int theme, String title,
                        String cancelTitle, int cancelTxtColor, List<ActionItem> otherBtns, OnActionClick l) {
        super(context, theme);
        this.context = context;
        mTitle = title;
        mCancelTitle = cancelTitle;
        this.cancelTextColor = cancelTxtColor;
        mOtherBtnTitles = otherBtns;
        mClickListener = l;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public void show() {
        super.show();
        initTitle();
    }

    @SuppressLint("InflateParams")
    private void initTitle() {
        if (!StringUtil.isNullOrEmpty(mTitle)) {
            if (tvTitle == null) {
                View titleView = this.getLayoutInflater().inflate(
                        R.layout.alert_dialog_menu_list_layout_title, null);
                tvTitle = (TextView) titleView.findViewById(R.id.popup_text);
            }
            tvTitle.setText(mTitle);
        }
    }

    private void initCancel() {
        if (!StringUtil.isNullOrEmpty(mCancelTitle)) {
            if (tvCancel == null) {
                tvCancel = (TextView) findViewById(R.id.actoin_sheet_cancel);
            }
            tvCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mClickListener.onClick(ActionDialog.this, ID_CANCEL, null);
                }
            });
            tvCancel.setText(mCancelTitle);
            tvCancel.setVisibility(View.VISIBLE);
            if (cancelTextColor > 0) {
                tvCancel.setTextColor(context.getResources().getColor(cancelTextColor));
            }
        } else {
            tvCancel.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alert_dialog_menu_layout);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = DensityUtil.getWidth();
        ListView mContentList = (ListView) this.findViewById(R.id.content_list);
        initTitle();
        initCancel();

        ActionAdapter mAdapter = new ActionAdapter();
        mAdapter.setAbsView(mContentList);
        mContentList.setAdapter(mAdapter);

        mAdapter.add(mOtherBtnTitles);
    }

    private class ActionAdapter extends BaseListAdapter<ActionItem> {

        public ActionAdapter() {
            super();
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            ActionItem item = this.getItem(position);
            if (item.id < 0) {
                return item.id;
            }
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActionItem item = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.alert_dialog_menu_list_layout, null);
                Holder holder = new Holder();
                holder.textView = (TextView) convertView
                        .findViewById(R.id.popup_text);
                holder.textView.setOnClickListener(holder);
                convertView.setTag(holder);
            }

            Holder holder = (Holder) convertView.getTag();
            holder.item = item;
            holder.textView.setText(item.name);
            if (item.textColor != null) {
                holder.textView.setTextColor(context.getResources().getColor(item.textColor));
            } else {
                holder.textView.setTextColor(context.getResources().getColor(R.color.color_sc));
            }

            return convertView;
        }

        @Override
        protected void recycleView(View view) {

        }

    }

    private class Holder implements View.OnClickListener {
        TextView textView;
        ActionItem item;

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onClick(ActionDialog.this, item.id, item);
            }
        }
    }

}