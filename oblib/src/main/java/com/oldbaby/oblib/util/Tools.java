package com.oldbaby.oblib.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    /**
     * 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：CMWAP 3：CMNET
     *
     * @param context
     * @return
     */
    public static int isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return 0;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            String extraInfo = netWorkInfo.getExtraInfo();
                            if ("cmwap".equalsIgnoreCase(extraInfo)
                                    || "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
                                return 2;
                            }
                            return 3;
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：2 2G : 3 3G : 4 4G
     *
     * @param context
     * @return
     */
    public static int isNetworkType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return 0;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            int networkClass = getNetworkClass(netWorkInfo
                                    .getSubtype());
                            return ++networkClass;
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static boolean isNetworkStrong(Context context) {
        int networkType = isNetworkType(context);
        //wifi和4g情况下认为网络情况良好
        if (networkType == 1 || networkType == 4) {
            return true;
        }
        return false;
    }

    private static final int NETWORK_CLASS_UNKNOWN = -1;
    private static final int NETWORK_CLASS_2_G = 1;
    private static final int NETWORK_CLASS_3_G = 2;
    private static final int NETWORK_CLASS_4_G = 3;

    /**
     * Return general class of network type, such as "3G" or "4G". In cases
     * where classification is contentious, this method is conservative.
     */
    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    /**
     * @param filePath
     * @return
     */
    public static Bitmap getImageFile(String filePath) {
        File file = new File(filePath);
        FileInputStream inStream = null;
        Bitmap bitmap = null;
        try {
            inStream = new FileInputStream(file);
            bitmap = (Bitmap) BitmapFactory.decodeStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                    inStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 得到当前文件夹下面所有的文件�?/ public static Vector getChildrenFile(String currentDir)
     * { Vector strVector = new Vector(); try { File tempfile = new
     * File(currentDir); tempfile.mkdirs(); String[] childname =
     * tempfile.list(); int i = childname.length; for(int j=0; j<i; j++) {
     * strVector.add(childname[j]); } return strVector; } catch(Exception e) {
     * return null; } }
     * <p>
     * public static String getImageFileName(String source){ if(source == null
     * || source.equals("") || "null".equalsIgnoreCase(source)) return "";
     * String imageName = source.substring(source.lastIndexOf("/") + 1); int
     * index = imageName.indexOf("."); return imageName.substring(0, index); }
     * <p>
     * /** 字节转化图片
     */
    public static SoftReference<Bitmap> bytesToBitmap(byte[] bitmapBytes,
                                                      Options opts, boolean flag) {
        SoftReference<Bitmap> soft = null;
        Bitmap bitmap = null;
        if (bitmapBytes.length != 0) {
            try {
                if (flag) {
                    bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0,
                            bitmapBytes.length, opts);
                } else {
                    bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0,
                            bitmapBytes.length);
                }
            } catch (OutOfMemoryError e) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    System.gc();
                }
                e.printStackTrace();
                return null;
            }
            if (bitmap != null) {
                soft = new SoftReference<Bitmap>(bitmap);
                bitmap = null;
            }
        }
        return soft;
    }

    /**
     * 读取文件转换byte[]
     *
     * @param currentDir
     * @param currentFile
     * @return
     */
    public static byte[] getDataFromFile(String currentDir, String currentFile) {
        String fileLocation = currentDir + currentFile;
        return getDataFromFile(fileLocation);
    }

    /**
     * 读取文件转换byte[]
     *
     * @return
     */
    public static byte[] getDataFromFile(String fileLocation) {
        File getFile = new File(fileLocation);
        FileInputStream fileIps = null;
        byte[] data = null;
        int length = 0;
        try {
            fileIps = new FileInputStream(getFile);
            length = (int) getFile.length();
            if (fileIps != null && length != 0) {
                data = new byte[length];
                fileIps.read(data);
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            if (fileIps != null) {
                try {
                    fileIps.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * 图片转化PNG字节
     *
     * @return
     */
    public static byte[] bitmapToPNGBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 图片转化JPEG字节
     *
     * @return
     */
    public static byte[] bitmapToJPEGBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // create the new Bitmap object
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        return resizedBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float round) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = round;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 将byte[]写入文件
     *
     * @param currentDir
     * @param currentFile
     * @param imgData
     * @return
     */
    public static Boolean saveDataToFile(String currentDir, String currentFile,
                                         byte[] imgData) {
        FileOutputStream fileOps = null;
        File currentDirFile = new File(currentDir);
        currentDirFile.mkdirs();
        File saveFile;
        try {
            saveFile = new File(currentDir + currentFile);
            fileOps = new FileOutputStream(saveFile);
            fileOps.write(imgData);
            fileOps.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileOps != null)
                    fileOps.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float newWidth,
                                      float newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // create the new Bitmap object
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);

        return resizedBitmap;
    }

    public static String renameImageUrl(String url) {
        if (url == null || url.equals(""))
            return "";
        int index = url.lastIndexOf("_") + 1;
        if (index != -1) {
            url = url.substring(0, index) + "90x90.jpg";
        }
        return url;
    }

    public static ArrayList<String> getDaysByMonthYear(String month, String year) {
        ArrayList<String> dayListStr = new ArrayList<String>();
        int monthInt = Integer.valueOf(month);
        int yearInt = Integer.valueOf(year);
        int flag = yearInt % 4;
        if (monthInt == 1 || monthInt == 3 || monthInt == 5 || monthInt == 7
                || monthInt == 8 || monthInt == 10 || monthInt == 12) {
            for (int i = 0; i < 31; i++) {
                dayListStr.add(i, (i + 1) + "");
            }
            return dayListStr;
        } else if (monthInt == 2 && flag == 0) {
            for (int i = 0; i < 29; i++) {
                dayListStr.add(i, (i + 1) + "");
            }
            return dayListStr;
        } else if (monthInt == 2 && flag != 0) {
            for (int i = 0; i < 28; i++) {
                dayListStr.add(i, (i + 1) + "");
            }
            return dayListStr;
        } else {
            for (int i = 0; i < 30; i++) {
                dayListStr.add(i, (i + 1) + "");
            }
            return dayListStr;
        }
    }

    public static int getFlagInArray(ArrayList<String> strs, String str) {
        if (strs.size() != 0 && strs != null) {
            for (int i = 0; i < strs.size(); i++) {
                if (strs.get(i).equals(str)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 是否第一次进入正文页�?
     *
     * @param context
     * @return
     */
    public static boolean theFristContent(Context context) {
        SharedPreferences shared = context.getSharedPreferences(
                "first_content", Context.MODE_PRIVATE);
        return shared.getBoolean("value", false);
    }

    public static void theFristContent(Context context, boolean value) {
        SharedPreferences shared = context.getSharedPreferences(
                "cn.cmcc.t_preferences", Context.MODE_PRIVATE);
        Editor edit = shared.edit();
        edit.putBoolean("first_content", value);
        edit.commit();
    }

    /**
     * 是否第一次进入登录面
     *
     * @param context
     * @return
     */
    public static boolean isFristMLogin(Context context) {
        SharedPreferences shared = context.getSharedPreferences(
                "cn.cmcc.t_preferences", Context.MODE_PRIVATE);
        return shared.getBoolean("first_login", true);
    }

    public static void noFirstMLogin(Context context) {
        SharedPreferences shared = context.getSharedPreferences(
                "cn.cmcc.t_preferences", Context.MODE_PRIVATE);
        Editor edit = shared.edit();
        edit.putBoolean("first_login", false);
        edit.commit();
    }

    /**
     * 判断输入的是否为微博帐号
     */
    public static boolean isScreamName(String str) {
        String cchar = "^[a-zA-Z\\d_]{5,15}$";
        Pattern p = Pattern.compile(cchar);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断输入的是否为密码
     */
    public static boolean isPassword(String str) {
        String cchar = "^[a-zA-Z\\d]{6,16}$";
        Pattern p = Pattern.compile(cchar);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断输入的是否为中文
     */
    public static boolean isChineseChar(String str) {
        String cchar = "^[\\u4e00-\\u9fa5a-zA-Z\\d]{1,10}$";
        Pattern p = Pattern.compile(cchar);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断输入的是否为手机
     **/
    public static boolean isPhoneNum(String str) {
        String s = "^(13[0-9]|15[0|1|2|3|4|5|6|7|8|9]|18[5|6|7|8|9])\\d{8}$";
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 根据选取的图片URI获取图片的具体地址
     *
     * @param mActivity
     * @param uri
     * @return
     */
    public static String getKnownFilePath(Activity mActivity, Uri uri) {
        String[] filePathColumn = new String[1];

        filePathColumn[0] = MediaStore.Images.Media.DATA;

        Cursor cursor = mActivity.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        if (filePath == null)
            filePath = uri.getPath();
        return filePath;
    }

    /**
     * 检查权限是否在清单文件注册
     *
     * @param context        上下文
     * @param permission     权限
     * @param appPackageName 应用包名
     * @return
     */
    public static boolean checkPermission(Context context, String permission, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        boolean perm = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permission, appPackageName));
        return perm;
    }

    /**
     * 23版本下检查摄像头权限是否被禁用
     *
     * @return
     */
    public static boolean checkCameraPermission() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }
        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 判断是否开启飞行模式
     */
    public static boolean isAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0);
        return (isAirplaneMode == 1) ? true : false;
    }

}
