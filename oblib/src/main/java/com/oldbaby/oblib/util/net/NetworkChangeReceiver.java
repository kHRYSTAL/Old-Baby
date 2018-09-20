package com.oldbaby.oblib.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            if (networkInfo.getState() == NetworkInfo.State.CONNECTED
                    || networkInfo.getState() == NetworkInfo.State.DISCONNECTED
                    || networkInfo.getState() == NetworkInfo.State.UNKNOWN
                    || networkInfo.getState() == NetworkInfo.State.SUSPENDED) {
                ConnectionManager.getInstance().verifyNetworkType();
            }
        }
    }

}
