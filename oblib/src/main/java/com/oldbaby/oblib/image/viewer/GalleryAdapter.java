package com.oldbaby.oblib.image.viewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.oldbaby.oblib.R;
import com.oldbaby.oblib.bitmap.ImageLoadListener;
import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.view.ImageViewEx;

import java.util.ArrayList;

class GalleryAdapter extends BaseAdapter {

    // private final List<ImgBrowsable> gallery;
    protected LayoutInflater inflater = null;
    private final ImageDataAdapter dataAdapter;

    ArrayList<View> mScrapHeap = new ArrayList<View>();
    private int descVisible = 1;
    private Context context;
    private LazyHeaders headers;

    public GalleryAdapter(Context context, ImageDataAdapter dataAdapter) {
        this.context = context;
        this.dataAdapter = dataAdapter;

        this.inflater = (LayoutInflater) OGApplication.APP_CONTEXT
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (dataAdapter != null) {
            return dataAdapter.count();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (dataAdapter != null) {
            int drawableId = dataAdapter.getDrawableId(position);
            if (drawableId > 0) {
                return drawableId;
            } else {
                return dataAdapter.getUrl(position);
            }
        }
        return null;
    }

    protected String getDesc(int position) {
        if (dataAdapter != null) {
            return dataAdapter.getDesc(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private View getNoUsedView() {
        if (mScrapHeap.size() < 4) {
            return null;
        }
        for (int i = 0; i < mScrapHeap.size(); i++) {
            if (mScrapHeap.get(i).getParent() == null) {
                return mScrapHeap.get(i);
            }
        }
        return null;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getNoUsedView();
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.picnews_item, null);
            holder = new Holder();
            holder.simage = (ImageViewEx) convertView
                    .findViewById(R.id.gallerysimage);
            holder.image = (ImageViewEx) convertView
                    .findViewById(R.id.galleryimage);
            holder.pro = (ProgressBar) convertView
                    .findViewById(R.id.galleryprogress);
            holder.tvDesc = (TextView) convertView.findViewById(R.id.tvImgViewerDesc);
            holder.svImgViewerDesc = (ScrollView) convertView.findViewById(R.id.svImgViewerDesc);

            convertView.setTag(holder);
            mScrapHeap.add(convertView);
        } else {
            holder = (Holder) convertView.getTag();

        }
        holder.image.setImageBitmap(null);
        holder.simage.setImageBitmap(null);
        holder.pro.setVisibility(View.VISIBLE);
        holder.tvDesc.setText(getDesc(position));
        holder.setDescVisible(descVisible);

        Object item = this.getItem(position);
        if (item instanceof Integer) {
            int drawableId = (int) item;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
            holder.image.setImageBitmap(bitmap);
            holder.onLoadFinished("", ImageLoadListener.STATUS_SUCCESS);
        } else if (item instanceof String) {
            String url = (String) item;
            Glide.with(context).asDrawable().load(headers != null ? new GlideUrl(url, headers) : url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.onLoadFinished("", ImageLoadListener.STATUS_FAIL);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.onLoadFinished("", ImageLoadListener.STATUS_SUCCESS);
                            return false;
                        }
                    })
                    .into(holder.image);
        } else {
            holder.onLoadFinished("", ImageLoadListener.STATUS_FAIL);
        }

        return convertView;
    }

    public void setHeader(LazyHeaders headers) {
        this.headers = headers;
    }

    /**
     * 设置描述的是否可见
     *
     * @param descVisible
     */
    public void setDescVisible(int descVisible) {
        this.descVisible = descVisible;
        for (View view : mScrapHeap) {
            Object obj = view.getTag();
            if (view.getParent() != null && (obj instanceof Holder)) {
                ((Holder) obj).setDescVisible(descVisible);
            }
        }
    }
}