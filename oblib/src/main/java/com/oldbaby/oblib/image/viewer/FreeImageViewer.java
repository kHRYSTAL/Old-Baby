package com.oldbaby.oblib.image.viewer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.oldbaby.oblib.R;
import com.oldbaby.oblib.component.act.BaseFragmentActivity;
import com.oldbaby.oblib.component.act.TitleType;
import com.oldbaby.oblib.image.GalleryListener;
import com.oldbaby.oblib.image.NewsGallery;
import com.oldbaby.oblib.rxjava.RxBus;
import com.oldbaby.oblib.util.DensityUtil;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.oblib.util.file.FileMgr;
import com.oldbaby.oblib.util.file.FileUtil;
import com.oldbaby.oblib.view.dialog.ActionDialog;
import com.oldbaby.oblib.view.dialog.ActionItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * 浏览图片集的大图，支持图片随手势放大缩小，也可支持删除
 * <p/>
 * 使用方法： FreeImageViewer.invoke(Context, ArrayList<ZHPicture>, curIndex,
 * maxIndex, rightButtonId, reqCode);
 * 参数含义：Context/图片集合/图片当前位置/图片总数/titlebar右侧按钮（现在支持保存、删除），可为空/请求码
 * <p/>
 * 删除图集中图片返回删除位置,RESULT_CODE = FreeImageViewer.POST_IMAGE_DELETE int deleteIndex
 * = data.getIntExtra(FreeImageViewer.TO_INDEX, -1)；
 */
public class FreeImageViewer extends BaseFragmentActivity implements GalleryListener {

    // 显示titlebar样式
    public static final int TYPE_SHOW_TITLE_BAR = 1;
    // 显示只有数字的样式
    public static final int TYPE_SHOW_NUMBER = 2;

    public static final String IMAGES = "freeimages";
    public static final String CUR_INDEX = "cur_index";
    public static final String MAX_INDEX = "max_index";
    public static final String RIGHT_INDEX = "btn_index";
    public static final String INK_TYPE = "ink_type";
    public static final String INK_REQ_NONCE = "ink_request_nonce";
    public static final String INK_IMAGE_HEADERS = "ink_image_header";

    public static final String DELETE_URLS = "delete_urls";

    /**
     * 右侧按钮，支持保存，删除功能
     */
    public static final int BUTTON_SAVE = 100;
    public static final int BUTTON_DELETE = 101;

    /**
     * 用户删除RESULT_CODE
     */
    public static final int POST_IMAGE_DELETE = 1009;

    private static final String TAG_DELETE = "tag_delete";

    public static int screenWidth;
    public static int screenHeight;
    private NewsGallery gallery;
    private GalleryAdapter adapter;
    private boolean isFullScreen = false;
    private RelativeLayout titleBar;

    private TextView titleBarTitle;
    private ImageView titleBarBack;
    private TextView titleBarRightBtn;
    private TextView tvNumber;

    private int rightBtnIndex = 0;
    private int curIndex = 0;
    private int maxIndex = 0;
    private String requestNonce;
    private HashMap<String, String> headers;

    private ImageDataAdapter urls;
    private ArrayList<String> deleteUrls;
    private ActionDialog actionDlg;
    private LazyHeaders.Builder builder;
    private int type;
    private LazyHeaders lazyHeaders;

    /**
     * 浏览指定的图片集合
     *
     * @param context
     * @param adapter
     * @param curIndex
     * @param maxIndex
     * @param rightButtonId FreeImageViewer.BUTTON_DELET
     * @param reqCode
     * @param type          FreeImageViewer.TYPE_SHOW_TITLE_BAR
     */
    public static void invoke(Activity context, ImageDataAdapter adapter,
                              int curIndex, int maxIndex, int rightButtonId, int reqCode, int type, HashMap<String, String> headers) {
        Intent intent = new Intent(context, FreeImageViewer.class);
        intent.putExtra(IMAGES, adapter);
        intent.putExtra(CUR_INDEX, curIndex);
        intent.putExtra(MAX_INDEX, maxIndex);
        intent.putExtra(RIGHT_INDEX, rightButtonId);
        intent.putExtra(INK_TYPE, type);
//        intent.putExtra(INK_IMAGE_HEADERS, headers);
        context.startActivityForResult(intent, reqCode);
    }

    /**
     * 通过事件通知调起服务，替代startActivityForResult
     *
     * @param context
     * @param adapter
     * @param curIndex
     * @param maxIndex
     * @param rightButtonId
     * @param requestNonce
     * @param type
     */
    public static void invoke(Activity context, ImageDataAdapter adapter,
                              int curIndex, int maxIndex, int rightButtonId, String requestNonce, int type, HashMap<String, String> headers) {
        Intent intent = new Intent(context, FreeImageViewer.class);
        intent.putExtra(IMAGES, adapter);
        intent.putExtra(CUR_INDEX, curIndex);
        intent.putExtra(MAX_INDEX, maxIndex);
        intent.putExtra(RIGHT_INDEX, rightButtonId);
        intent.putExtra(INK_TYPE, type);
        intent.putExtra(INK_REQ_NONCE, requestNonce);
        intent.putExtra(INK_IMAGE_HEADERS, headers);
        context.startActivityForResult(intent, 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onContinueCreate(Bundle savedInstanceState) {
        super.onContinueCreate(savedInstanceState);

        urls = (ImageDataAdapter) this.getIntent()
                .getSerializableExtra(IMAGES);
        if (urls == null || urls.count() < 1) {
            this.finish();
            return;
        }
        curIndex = getIntent().getIntExtra(CUR_INDEX, 0);
        maxIndex = getIntent().getIntExtra(MAX_INDEX, 0);
        rightBtnIndex = getIntent().getIntExtra(RIGHT_INDEX, 0);
        headers = (HashMap<String, String>) getIntent().getSerializableExtra(INK_IMAGE_HEADERS);
        type = getIntent().getIntExtra(INK_TYPE, TYPE_SHOW_TITLE_BAR);
        requestNonce = getIntent().getStringExtra(INK_REQ_NONCE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.free_image_viewer);

        gallery = (NewsGallery) findViewById(R.id.gallery);
        gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
        gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
        gallery.setGestureListener(this);

        if (headers != null) {
            builder = new LazyHeaders.Builder();
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
            lazyHeaders = builder.build();
        }
        adapter = new GalleryAdapter(this, urls);
        adapter.setHeader(lazyHeaders);
        gallery.setAdapter(adapter);
        gallery.setSelection(curIndex, true);

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                setTitleText(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        gallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showSaveActionDialog();
                return false;
            }
        });

        tvNumber = (TextView) findViewById(R.id.tvNumber);
        titleBar = (RelativeLayout) findViewById(R.id.navigation);
        titleBarTitle = (TextView) findViewById(R.id.titledes);
        titleBarBack = (ImageView) findViewById(R.id.gallery_back);
        titleBarBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeViewer();
            }
        });
        titleBarRightBtn = (TextView) findViewById(R.id.gallery_action);
        titleBarRightBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (rightBtnIndex) {
                    case BUTTON_SAVE: {
                        saveImage();
                    }
                    break;
                    case BUTTON_DELETE:
                        showConfirmDlg(TAG_DELETE, "确认删除图片吗？", "确定", "取消", null);
                        break;
                }

            }
        });
        screenWidth = DensityUtil.getWidth();
        screenHeight = DensityUtil.getHeight();

        switch (type) {
            case TYPE_SHOW_TITLE_BAR:
                titleBar.setVisibility(View.VISIBLE);
                setRightBtnText();
                break;
            case TYPE_SHOW_NUMBER:
                tvNumber.setVisibility(View.VISIBLE);
                break;
        }
        setTitleText(curIndex);
    }

    /**
     * 显示保存图片的ActionDialog
     */
    private void showSaveActionDialog() {
        if (actionDlg == null) {
            ArrayList<ActionItem> actions = new ArrayList<>();
            actions.add(new ActionItem(1, R.color.color_dc, "保存到本地相册"));
            actionDlg = new ActionDialog(FreeImageViewer.this, R.style.ActionDialog,
                    null, "取消", -1, actions, new ActionDialog.OnActionClick() {
                @Override
                public void onClick(DialogInterface dialog, int id, ActionItem item) {
                    if (actionDlg != null)
                        if (actionDlg.isShowing()) {
                            actionDlg.dismiss();
                        }
                    switch (id) {
                        case 1:
                            saveImage();
                            break;
                    }
                }
            });
            WindowManager.LayoutParams wmlp = actionDlg.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM;
        }
        if (!actionDlg.isShowing()) {
            actionDlg.show();
        }
    }

    @Override
    protected int titleType() {
        return TitleType.TITLE_NONE;
    }

    @Override
    protected void configStartAnim(Intent intent) {

    }

    @Override
    public void updateStatusBarColor(int resId) {

    }

    @Override
    public void updateTitleBarColor(int resId) {

    }

    @Override
    public void updateTitleBarAndStatusBar(int resId) {

    }

    public void setRightBtnText() {
        switch (rightBtnIndex) {
            case BUTTON_SAVE:
                if (titleBarRightBtn != null) {
                    titleBarRightBtn.setText("保存");
                }
                break;
            case BUTTON_DELETE:
                if (titleBarRightBtn != null) {
                    titleBarRightBtn.setText("删除");
                }
                break;
            default:
                titleBarRightBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 设置标题
     */
    public void setTitleText(int cur) {
        switch (type) {
            case TYPE_SHOW_TITLE_BAR:
                if (cur >= 0 && maxIndex > 0 && cur < maxIndex) {
                    titleBarTitle.setText(cur + 1 + "/" + maxIndex);
                    curIndex = cur;
                }
                break;
            case TYPE_SHOW_NUMBER:
                if (cur >= 0 && maxIndex > 0 && cur < maxIndex) {
                    tvNumber.setText(cur + 1 + "/" + maxIndex);
                    curIndex = cur;
                }
                break;
        }
    }

    /**
     * 保存图片
     */
    private void saveImage() {
        if (FileUtil.isExternalMediaMounted()) {

            if (adapter == null)
                return;
            Object item = adapter.getItem(gallery.getSelectedItemPosition());
            if (item instanceof Integer) {
                // 保存drawable
                int drawableId = (int) item;
                BitmapDrawable d = (BitmapDrawable) getResources().getDrawable(drawableId);
                Bitmap img = d.getBitmap();
                FileUtil.saveBitmapToSDCard(img, UUID.randomUUID().toString());
                ToastUtil.showLong("已保存到相册");
            } else if (item instanceof String) {
                final String url = (String) item;
                new Thread() {
                    @Override
                    public void run() {
                        FutureTarget<File> future = Glide.with(FreeImageViewer.this)
                                .load(lazyHeaders != null ? new GlideUrl(url, lazyHeaders) : url)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                        String path = "";
                        try {
                            File cacheFile = future.get();
                            path = cacheFile.getAbsolutePath();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (StringUtil.isNullOrEmpty(path))
                            return;
                        String imageName = FileUtil.convertFileNameFromUrl(url);
                        File sdcardFileDir = FileMgr.Instance().getDir(FileMgr.DirType.IMAGE);

                        File sdcardFile = new File(sdcardFileDir, imageName + ".jpg");
                        FileUtil.copyFile(new File(path), sdcardFile);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(sdcardFile)));
                        ToastUtil.showLong("已保存到相册");
                    }
                }.start();
            } else {
                ToastUtil.showLong("图片格式错误");
            }
        } else {
            ToastUtil.showLong("请插入SD卡");
        }
    }

    @Override
    public void onSingleTabUp() {
        switch (type) {
            case TYPE_SHOW_TITLE_BAR:
                if (isFullScreen) {
                    titleBar.setVisibility(View.INVISIBLE);
                    adapter.setDescVisible(0);
                } else {
                    titleBar.setVisibility(View.VISIBLE);
                    adapter.setDescVisible(1);
                }
                isFullScreen = !isFullScreen;
                break;
            case TYPE_SHOW_NUMBER:
                FreeImageViewer.this.finish();
                break;
        }
    }


    @Override
    public void onOkClicked(Context context, String tag, Object arg) {
        hideConfirmDlg(TAG_DELETE);
        deleteImage();
    }

    @Override
    public void onNoClicked(Context context, String tag, Object arg) {
        hideConfirmDlg(TAG_DELETE);
    }

    /**
     * 删除图片
     */
    private void deleteImage() {
        int position = gallery.getSelectedItemPosition();
        String deleteUrl = urls.getUrl(position);
        if (deleteUrls == null) {
            deleteUrls = new ArrayList<>();
        }
        deleteUrls.add(deleteUrl);

        if (adapter.getCount() == 1) {
            urls.remove(position);
            closeViewer();
        } else {
            if (curIndex == (maxIndex - 1)) {
                curIndex--;
            }
            maxIndex--;
            setTitleText(curIndex);

            urls.remove(position);
            adapter = new GalleryAdapter(this, urls);
            gallery.setAdapter(adapter);
            gallery.setSelection(curIndex, false);
        }
    }

    /**
     * 关闭浏览
     */
    private void closeViewer() {
        Intent intent = new Intent();
        intent.putExtra(DELETE_URLS, deleteUrls);
        setResult(POST_IMAGE_DELETE, intent);
        FreeImageViewer.this.finish();

        //通过事件通知进行回调
        if (requestNonce != null) {
            BrowseImgResultEvent event = new BrowseImgResultEvent(requestNonce, deleteUrls);
            RxBus.getDefault().post(event);
        }
    }
}