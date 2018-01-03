package github.shawlaw.app.callintwo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import github.shawlaw.app.callintwo.services.LoadNotificationService;

/**
 * 监听开机启动事件
 * @author Shawlaw
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                    || "Shawlaw.action.BOOT_COMPLETED".equals(intent.getAction())) {
                LoadNotificationService.start(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
