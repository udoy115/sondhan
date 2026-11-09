package com.sondhan.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.util.Log;

public class SystemStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemStatusReceiver";

    public interface SystemStatusListener {
        void onAirplaneModeChanged(boolean isAirplaneModeOn);
        void onBatteryStatusChanged(boolean isLowBattery);
        void onNetworkStatusChanged(boolean isConnected);
    }

    private static SystemStatusListener activeListener;

    public static void setListener(SystemStatusListener listener) {
        activeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        String action = intent.getAction();
        Log.d(TAG, "Received system broadcast action: " + action);

        // 1. Airplane Mode Transition
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            Log.d(TAG, "Airplane mode state changed: " + isAirplaneModeOn);
            if (activeListener != null) {
                activeListener.onAirplaneModeChanged(isAirplaneModeOn);
            }
        }
        // 2. Battery Low Transition
        else if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            Log.d(TAG, "Device battery is LOW");
            if (activeListener != null) {
                activeListener.onBatteryStatusChanged(true);
            }
        }
        // 3. Battery Restored Transition
        else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            Log.d(TAG, "Device battery is OKAY");
            if (activeListener != null) {
                activeListener.onBatteryStatusChanged(false);
            }
        }
        // 4. Connectivity Transition
        else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            boolean isConnected = checkNetwork(context);
            Log.d(TAG, "Network connectivity changed: " + isConnected);
            if (activeListener != null) {
                activeListener.onNetworkStatusChanged(isConnected);
            }
        }
    }

    public static boolean checkNetwork(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            android.net.Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
            return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } catch (Exception e) {
            return true;
        }
    }
}

