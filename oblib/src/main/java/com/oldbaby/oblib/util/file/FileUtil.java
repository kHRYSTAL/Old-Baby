package com.oldbaby.oblib.util.file;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;


import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.Base64;
import com.oldbaby.oblib.util.MD5;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * 文件工具类,保存文件，删除文件
 */
public class FileUtil {

    private static final String TAG = "fileutil";

    /**
     * 判断sdcard是否可用
     */
    public static boolean isExternalMediaMounted() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 将一个字符串写进一个文件
     */
    public static boolean writeStringToFile(File file, String content) {
        boolean res = false;
        OutputStream fos = null;
        OutputStreamWriter outs = null;
        try {
            fos = new FileOutputStream(file);
            outs = new OutputStreamWriter(fos, "UTF-8");
            outs.write(content);
            outs.flush();
            res = true;
        } catch (Exception ex) {
            MLog.e(TAG, ex.getMessage(), ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 保存一个bitmap到sdcard上
     */
    public static String saveBitmapToSDCard(Bitmap bitmap, String url) {
        if (bitmap == null || bitmap.isRecycled())
            return null;

        String filePath = FileMgr.Instance().getFilepath(FileMgr.DirType.IMAGE,
                convertFileNameFromUrl(url) + ".jpg");
        FileOutputStream baos = null;
        try {
            baos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            baos.flush();
            baos.close();

            OGApplication.APP_CONTEXT.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                    .fromFile(new File(filePath))));
        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    /**
     * 保存bitmap到指定的路径
     */
    public static String saveBitmapToFile(Bitmap bitmap, FileMgr.DirType dir,
                                          String name) {
        if (bitmap == null || bitmap.isRecycled())
            return null;

        String filePath = FileMgr.Instance().getFilepath(dir, name);
        FileOutputStream baos = null;
        try {
            baos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    public static int clearDir(String path) {
        File file = new File(path);
        return clearDir(file);
    }

    /**
     * 清除指定文件目录
     */
    public static int clearDir(File dir) {
        if (dir == null || !dir.exists())
            return 0;

        int count = 0;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0, l = files.length; i < l; i++) {
                    count += clearDir(files[i]);
                }
            }
        }

        dir.delete();
        count++;

        return count;
    }

    /**
     * 删除指定文件
     */
    public static boolean deleteFile(final String path) {
        File f = new File(path);
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }

    /**
     * 将url用MD5转化为可以保存的字符串
     */
    public static String convertFileNameFromUrl(String fileUrl) {
        if (StringUtil.isNullOrEmpty(fileUrl)) {
            return null;
        }
        return MD5.getMD5(fileUrl);
    }

    /**
     * 转换文件大小
     */
    public static String formatFileSize(long fileSize) {
        if (fileSize == 0) {
            return "0B";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 复制文件
     *
     * @param original
     * @param destination
     * @return
     */
    public static boolean copyFile(final File original, final File destination) {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(original);
            os = new FileOutputStream(destination);

            return copyStream(is, os);
        } catch (final FileNotFoundException e) {
            MLog.e(TAG, "copy photo", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (final IOException e) {
                MLog.e(TAG, "close inputstream: ", e);
            }

        }
        return false;
    }

    private static boolean copyStream(final InputStream is,
                                      final OutputStream os) {
        try {
            final byte[] bs = new byte[is.available()];
            is.read(bs);

            os.write(bs);
            os.flush();
            return true;
        } catch (final FileNotFoundException e) {
            MLog.e(TAG, "copy photo", e);
        } catch (final IOException e) {
            MLog.e(TAG, "copy photo", e);
        }
        return false;
    }

    /**
     * 将指定的文件base64 编码
     *
     * @param path
     * @return
     */
    public static String encodeBase64File(String path) {
        File file = new File(path);
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            return android.util.Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            MLog.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            MLog.e(TAG, e.getMessage(), e);
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (Exception e) {
                    MLog.e(TAG, e.getMessage(), e);
                }
            }
        }
        return null;
    }

    /**
     * 将base64字符串解压到指定的文件
     *
     * @param base64Code
     * @param savePath
     * @return
     */
    public static boolean decoderBase64File(String base64Code, String savePath) {

        boolean result = false;
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            out.write(buffer);
            out.close();
            result = true;
        } catch (FileNotFoundException e) {
            MLog.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            MLog.e(TAG, e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    MLog.e(TAG, e.getMessage(), e);
                }
            }
        }
        return result;

    }

    public static long getFileOrDirSize(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.length();
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }
        long total = 0;
        for (int i = 0; i < files.length; i++) {
            total += getFileOrDirSize(files[i].getAbsolutePath());
        }
        return total;
    }
}
