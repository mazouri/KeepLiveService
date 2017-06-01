package com.mazouri.keepliveservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wangdongdong on 17-6-1.
 */

public class KeepLiveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_USER_BACKGROUND.equals(action)) {
            KeepLiveManager.getInstance().startKeepLiveActivity(context);
        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_BACKGROUND.equals(action)) {
            KeepLiveManager.getInstance().finishKeepLiveActivity();
        }
    }
}
