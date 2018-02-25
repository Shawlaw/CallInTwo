package github.shawlaw.app.callintwo.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import github.shawlaw.app.callintwo.services.LoadNotificationService;

/**
 * 监听开机启动事件
 * @author Shawlaw
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                LoadNotificationService.start(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableReceiver(Context context, boolean enable) {
        if (context != null) {
            context = context.getApplicationContext();
            ComponentName receiver = new ComponentName(context, BootCompleteReceiver.class);
            PackageManager pm = context.getPackageManager();
            int newState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
        }
    }
}
