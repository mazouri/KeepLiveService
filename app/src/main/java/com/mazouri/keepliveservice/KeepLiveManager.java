package com.mazouri.keepliveservice;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * Created by wangdongdong on 17-6-1.
 */

public class KeepLiveManager {

    private static KeepLiveManager instance;

    public WeakReference<KeepLiveActivity> mWeakActivityRef = null;

    public static KeepLiveManager getInstance() {
        if (instance == null) {
            instance = new KeepLiveManager();
        }
        return instance;
    }

    public void initKeepLiveActivity(KeepLiveActivity keepLiveActivity) {
        this.mWeakActivityRef = new WeakReference<>(keepLiveActivity);

        // 设置1像素透明窗口
        Window window = keepLiveActivity.getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;
        window.setAttributes(params);
    }

    /**
     * 启动一像素Activity
     * @param context
     */
    public void startKeepLiveActivity(Context context) {
        Intent intent = new Intent(context, KeepLiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 结束一像素Activity
     */
    public void finishKeepLiveActivity() {
        if (mWeakActivityRef != null) {
            Activity activity = mWeakActivityRef.get();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 启动JobScheduler拉活
     * 适用范围：android5.0以上
     */
    public void startJobScheduler(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                int jobId = 1;
                JobInfo.Builder builder = new JobInfo.Builder(jobId,
                        new ComponentName(App.get(),KeepLiveService.class));
                builder.setPeriodic(10);
                builder.setPersisted(true);
                JobScheduler jobScheduler = (JobScheduler)App.get().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 方案设计思想：Android 中 Service 的优先级为4，通过 setForeground 接口可以将后台 Service 设置为前台 Service，使进程的优先级由4提升为2，从而使进程的优先级仅仅低于用户当前正在交互的进程，与可见进程优先级一致，使进程被杀死的概率大大降低。
     * <p>
     * 方案实现挑战：从 Android2.3 开始调用 setForeground 将后台 Service 设置为前台 Service 时，必须在系统的通知栏发送一条通知，也就是前台 Service 与一条可见的通知时绑定在一起的。
     * <p>
     * 对于不需要常驻通知栏的应用来说，该方案虽好，但却是用户感知的，无法直接使用。
     * <p>
     * 方案挑战应对措施：通过实现一个内部 Service，在 LiveService 和其内部 Service 中同时发送具有相同 ID 的 Notification，然后将内部 Service 结束掉。随着内部 Service 的结束，Notification 将会消失，但系统优先级依然保持为2。
     * <p>
     * 方案适用范围：适用于目前已知所有版本。
     * <p>
     * 方案具体实现：
     *
     * @param keepLiveService
     * @param innerService
     */
    public void setForeground(Service keepLiveService, final Service innerService) {
        if (keepLiveService != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                keepLiveService.startForeground(1, new Notification());
            } else {
                keepLiveService.startForeground(1, new Notification());
                if (innerService != null) {
                    innerService.startForeground(1, new Notification());
                    innerService.stopSelf();
                }
            }
        }
    }
}
