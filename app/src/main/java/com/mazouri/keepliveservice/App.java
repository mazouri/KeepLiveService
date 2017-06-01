package com.mazouri.keepliveservice;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by wangdongdong on 17-6-1.
 */

public class App extends Application {
    private static App baseApplication;
    private String processName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        attach();
    }

    public static App get(){
        return baseApplication;
    }

    private void attach(){
        processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(getPackageName());
            if (defaultProcess) {
                startService(new Intent(this, KeepLiveService.class));
            } else if (processName.contains(":live")) {

            }
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
