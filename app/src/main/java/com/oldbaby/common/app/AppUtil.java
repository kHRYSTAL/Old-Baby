package com.oldbaby.common.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.oldbaby.oblib.util.MD5;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;
import com.tencent.mmkv.MMKV;

import java.util.List;
import java.util.UUID;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 18/9/29
 * update time:
 * email: 723526676@qq.com
 */
public class AppUtil {

    private static final String TAG = "apputil";
    private static final String PREF_DEVICE_ID = "pref_device_id_new";
    private static AppUtil instance;

    private AppUtil() {

    }

    public static AppUtil Instance() {
        if (instance == null) {
            synchronized (AppUtil.class) {
                if (instance == null) {
                    instance = new AppUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 程序是否运行
     *
     * @param excludeClassName 排除的Activity， 场景：推送的时候，UriBrowseActivity一定运行，所以需要排除此activity
     * @return true: 程序正在运行。false： 程序没有运行
     */
    public static boolean appIsRunning(String excludeClassName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) OldBabyApplication.APP_CONTEXT
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(OldBabyApplication.APP_CONTEXT.getPackageName()) &&
                    info.baseActivity.getPackageName().equals(OldBabyApplication.APP_CONTEXT.getPackageName())) {
                if (StringUtil.isNullOrEmpty(excludeClassName)) {
                    isRunning = true;
                } else {
                    String topName = info.topActivity.getClassName();
                    if (!topName.contains(excludeClassName)) {
                        isRunning = true;
                    }
                }
            }
        }
        return isRunning;
    }

    /**
     * 获取当前应用的版本号
     */
    public int getVersionCode() {
        PackageInfo pInfo;
        try {
            pInfo = OldBabyApplication.APP_CONTEXT
                    .getPackageManager()
                    .getPackageInfo(
                            OldBabyApplication.APP_CONTEXT.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            MLog.e(TAG, e.getMessage(), e);
        }
        return -1;
    }

    /**
     * 获取当前应用的版本号
     */
    public String getVersionName() {
        PackageInfo pInfo;
        try {
            pInfo = OldBabyApplication.APP_CONTEXT
                    .getPackageManager()
                    .getPackageInfo(
                            OldBabyApplication.APP_CONTEXT.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            MLog.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    /**
     * 用随机数做设备的 deviceId
     */
    public String getDeviceId() {

        String deviceId = MMKV.defaultMMKV().decodeString(PREF_DEVICE_ID, null);
        if (deviceId == null) {
            String code = UUID.randomUUID().toString();
            deviceId = MD5.getDecMD5(code);
            MMKV.defaultMMKV().encode(PREF_DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    /**
     * 获取手机厂商
     */
    public String getDeviceManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取手机品牌
     */
    public String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备型号
     */
    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }
}
