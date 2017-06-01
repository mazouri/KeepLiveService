package com.mazouri.keepliveservice;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wangdongdong on 17-6-1.
 */

public class KeepLiveService extends Service {
    private static final String TAG = KeepLiveService.class.getSimpleName();

    static KeepLiveService keepLiveService;
    private KeepLiveReceiver keepLiveReceiver;

    public KeepLiveService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        keepLiveService = this;
        registerBroadcast();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startService(new Intent(this,InnerService.class));
        Log.d(TAG, "onStartCommand intent action :" + intent  + ", flags : " + flags  + ", startId : " + startId);
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        try {
            unregisterReceiver(keepLiveReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        startService(new Intent(this, KeepLiveService.class));
        super.onDestroy();

    }

    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            KeepLiveManager.getInstance().setForeground(keepLiveService, this);
            return super.onStartCommand(intent, flags, startId);
        }
    }

    private void registerBroadcast(){
        if (keepLiveReceiver == null){
            keepLiveReceiver = new KeepLiveReceiver();
            IntentFilter receiverFilter=new IntentFilter();
            receiverFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            receiverFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            receiverFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            receiverFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            receiverFilter.addAction(Intent.ACTION_USER_PRESENT);
            receiverFilter.addAction(Intent.ACTION_SCREEN_ON);
            receiverFilter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(keepLiveReceiver, receiverFilter);
        }
    }
}
