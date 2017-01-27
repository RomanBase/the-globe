package com.base.wall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.base.wall.listener.NetworkConnectedListener;

public class ConnectionHandler extends BroadcastReceiver {

    private NetworkConnectedListener listener;

    public ConnectionHandler(NetworkConnectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected && listener != null) {
            listener.onConnected();
        }
    }
}
