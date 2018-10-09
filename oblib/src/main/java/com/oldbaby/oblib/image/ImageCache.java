package com.oldbaby.oblib.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;
import com.oldbaby.oblib.util.ToastUtil;
import com.oldbaby.oblib.util.file.FileMgr;
import com.oldbaby.oblib.util.file.FileUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/10/9
 * update time:
 * email: 723526676@qq.com
 */
public class ImageCache extends AsyncTask<String, Void, File> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final LazyHeaders headers;
    private String imgUrl;

    public ImageCache(Context context) {
        this.context = context;
        this.headers = null;
    }

    public ImageCache(Context context, LazyHeaders headers) {
        this.context = context;
        this.headers = headers;
    }

    @Override
    protected File doInBackground(String... strings) {
        imgUrl = strings[0];
        File cacheFile = null;
        FutureTarget<File> future = Glide.with(context)
                .load(headers != null ? new GlideUrl(imgUrl, headers) : imgUrl)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        String path = "";
        try {
            cacheFile = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return cacheFile;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (file == null) {
            ToastUtil.showLong("图片保存失败");
            return;
        }
        String path = file.getAbsolutePath();
        if (StringUtil.isNullOrEmpty(path))
            return;
        String imageName = FileUtil.convertFileNameFromUrl(imgUrl);
        File sdcardFileDir = FileMgr.Instance().getDir(FileMgr.DirType.IMAGE);

        File sdcardFile = new File(sdcardFileDir, imageName + ".jpg");

        FileUtil.copyFile(new File(path), sdcardFile);
        MLog.e("ImageCache", path, sdcardFile.getAbsolutePath());
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(sdcardFile)));
        ToastUtil.showLong("已保存到相册");
    }
}
