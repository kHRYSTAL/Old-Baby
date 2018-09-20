package com.oldbaby.oblib.util.net;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.oldbaby.oblib.component.application.OGApplication;
import com.oldbaby.oblib.util.MLog;
import com.oldbaby.oblib.util.StringUtil;

import java.util.Locale;

public class ConnectionManager {

    public static final int NETWORK_TYPE_2G = 0;
    public static final int NETWORK_TYPE_3G = 1;
    public static final int NETWORK_TYPE_WIFI = 2;

    public static final int NETWORK_OP_TYPE_WIFI = 0;
    public static final int NETWORK_OP_TYPE_CHINAMOBILE = 1;
    public static final int NETWORK_OP_TYPE_CHINAUNION = 2;
    public static final int NETWORK_OP_TYPE_CHINATEL = 3;

    private static ConnectionManager mInstance = null;

    /**
     * log tag.
     */
    private static final String TAG = ConnectionManager.class.getSimpleName();

    /**
     * log debug on/off .
     */
    private static final boolean DEBUG = false;

    /**
     * current apn.
     */
    private String mApn;

    /**
     * apn proxy.
     */
    private String mProxy;

    /**
     * proxy port.
     */
    private String mPort;

    /**
     * 网络类型.
     */
    private int mNetType;

    /**
     * 网络OP类型.
     */
    private int mNetOPType = NETWORK_OP_TYPE_WIFI;

    /**
     * 当前网络是否使用wap。 wifi 或者 cmnet等为不使用 wap.
     */
    private boolean mUseWap;

    private boolean mNetworkAvailable;

    private NetworkAvailableListener mAvailableListener;

    private final NetworkChangeReceiver mNetworkChangeReceiver;

    /**
     * prefered apn url.
     */
    public static final Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    protected ConnectionManager() {
        verifyNetworkType();
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        Context context = OGApplication.APP_CONTEXT.getApplicationContext();
        IntentFilter upIntentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkChangeReceiver, upIntentFilter);
    }

    public static ConnectionManager getInstance() {
        if (mInstance == null) {
            synchronized (ConnectionManager.class) {
                if (mInstance == null) {
                    mInstance = new ConnectionManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 检查当前网络类型。
     */
    public void verifyNetworkType() {

        long startTime = System.nanoTime();

        Context context = OGApplication.APP_CONTEXT.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {

            if (DEBUG) {
                MLog.d(TAG,
                        "network type : "
                                + activeNetInfo.getTypeName().toLowerCase(
                                Locale.getDefault()));
            }
            int netType = activeNetInfo.getType();
            int netSubtype = activeNetInfo.getSubtype();

            if (netType == ConnectivityManager.TYPE_WIFI) {
                mNetType = NETWORK_TYPE_WIFI;
                mUseWap = false;
            } else {
                mNetType = checkGNetworkType(netType, netSubtype);
                if (PackageManager.PERMISSION_GRANTED == context
                        .checkCallingOrSelfPermission(android.Manifest.permission.WRITE_APN_SETTINGS)) {
                    checkApn(context);
                }
            }
        }

        // 获取运营商类型
        if (mNetType == NETWORK_TYPE_WIFI) {
            mNetOPType = NETWORK_OP_TYPE_WIFI;
        } else {
            TelephonyManager telManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            if (!StringUtil.isNullOrEmpty(IMSI)) {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                    mNetOPType = NETWORK_OP_TYPE_CHINAMOBILE;
                } else if (IMSI.startsWith("46001")) {
                    mNetOPType = NETWORK_OP_TYPE_CHINAUNION;
                } else if (IMSI.startsWith("46003")) {
                    mNetOPType = NETWORK_OP_TYPE_CHINATEL;
                }
            }

        }

        if (DEBUG) {
            MLog.d(TAG,
                    String.format("verifyNetworkType time : %d ms",
                            (System.nanoTime() - startTime) / 1000));
        }
        if (activeNetInfo != null) {
            setNetworkAvailable(activeNetInfo.isConnected());
        } else {
            setNetworkAvailable(false);
        }
    }

    public boolean isNetworkConnected() {
        return mNetworkAvailable;
    }

    public void setNetworkAvailableListener(NetworkAvailableListener l) {
        mAvailableListener = l;
    }

    /**
     * 当前网络连接是否是wap网络。
     *
     * @return cmwap 3gwap ctwap 返回true
     */
    public boolean isWapNetwork() {
        return mUseWap;
    }

    /**
     * 获取当前网络连接apn.
     *
     * @return apn
     */
    public String getApn() {
        return mApn;
    }

    /**
     * 获得当前网络类型
     *
     * @return wifi, 2g, 3g ……
     */
    public int getNetType() {
        return mNetType;
    }

    public boolean isWifi() {
        return mNetType == NETWORK_TYPE_WIFI;
    }

    /**
     * 判断是不是非2G网络
     *
     * @return
     */
    public boolean isFastNet() {
        return mNetType == NETWORK_TYPE_3G || mNetType == NETWORK_TYPE_WIFI;
    }

    /**
     * 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172.
     *
     * @return 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172
     */
    public String getProxy() {
        return mProxy;
    }

    /**
     * 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80.
     *
     * @return 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80
     */
    public String getProxyPort() {
        return mPort;
    }

    // <add by fujiaxing 从updatecheckrunnable挪过来 BEGIN

    /**
     * 网络是否可用。
     *
     * @return 连接并可用返回 true
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public int getNetOPType() {
        return mNetOPType;
    }

    /**
     * 检查当前系统apn 设置 状态.
     *
     * @param context context
     */
    private void checkApn(Context context) {

        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI,
                new String[]{"_id", "apn", "proxy", "port"}, null, null,
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int anpIndex = cursor.getColumnIndex("apn");
                int proxyIndex = cursor.getColumnIndex("proxy");
                int portIndex = cursor.getColumnIndex("port");

                mApn = cursor.getString(anpIndex);
                mProxy = cursor.getString(proxyIndex);
                mPort = cursor.getString(portIndex);

                // mNetType = mApn;

                if (DEBUG) {
                    MLog.d(TAG, "apn: " + " " + mApn + " " + mProxy + " "
                            + mPort);
                }

                if (mProxy != null && mProxy.length() > 0) {
                    // 如果设置了代理

                    if ("10.0.0.172".equals(mProxy.trim())) {
                        // 当前网络连接类型为cmwap || uniwap
                        mUseWap = true;
                        mPort = "80";
                    } else if ("10.0.0.200".equals(mProxy.trim())) {
                        mUseWap = true;
                        mPort = "80";
                    } else {
                        // 否则为net
                        mUseWap = false;
                    }

                } else if (mApn != null) {
                    // 如果用户只设置了apn，没有设置代理，我们自动补齐

                    String strApn = mApn.toUpperCase(Locale.getDefault());
                    if (strApn.equals("CMWAP") || strApn.equals("UNIWAP")
                            || strApn.equals("3GWAP")) {
                        mUseWap = true;
                        mProxy = "10.0.0.172";
                        mPort = "80";
                    } else if (strApn.equals("CTWAP")) {
                        mUseWap = true;
                        mProxy = "10.0.0.200";
                        mPort = "80";
                    } else {
                        // 否则为net
                        mUseWap = false;
                    }

                } else {
                    // 其它网络都看作是net
                    mUseWap = false;
                }
                if (DEBUG) {
                    MLog.d(TAG, "adjust apn: " + " " + mApn + " " + mProxy
                            + " " + mPort);
                }
            }

            cursor.close();
        }

    }

    /**
     * 获取活动的连接。
     *
     * @return 当前连接
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        return connectivity.getActiveNetworkInfo();
    }

    // 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
    private int checkGNetworkType(int type, int subType) {

        int networkType = NETWORK_TYPE_2G;

        if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    networkType = NETWORK_TYPE_3G;
                    break;
                default:
                    networkType = NETWORK_TYPE_2G;
                    break;
            }
        }

        return networkType;
    }

    private void setNetworkAvailable(boolean available) {
        if (mNetworkAvailable != available) {
            mNetworkAvailable = available;
            if (mAvailableListener != null) {
                mAvailableListener.onNetworkAvailableChanged(mNetworkAvailable);
            }
        }
    }
}
