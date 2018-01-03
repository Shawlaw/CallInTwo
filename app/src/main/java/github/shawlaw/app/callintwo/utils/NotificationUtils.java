package github.shawlaw.app.callintwo.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import github.shawlaw.app.callintwo.R;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;

/**
 * Notification相关的工具类
 * @author Shawlaw
 * @date 2017/12/7
 */

public class NotificationUtils {
    private static String SP_NAME = "callInTwo";
    private static String SP_KEY_CHANNEL_CREATED = "channelCreated";
    private static String CHANNEL_ID_CALL = "CallNotification";
    private static String CHANNEL_NAME_CALL = "Call";

    public static void sendNotification(Context context, SelectedContactBean bean) {
        context = context.getApplicationContext();
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            initCallNotificationChannel(context, mNotifyMgr);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_CALL);
            builder.setContent(buildNotifyView(context, bean));
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.avatar_default);
            mNotifyMgr.notify(bean.getNotificationId().hashCode(), builder.build());
        }

    }

    private static void initCallNotificationChannel(@NonNull Context application,
                                                    @NonNull NotificationManager notifyMgr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SharedPreferences sp = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            boolean channelCreated = sp.getBoolean(SP_KEY_CHANNEL_CREATED, false);
            if (!channelCreated) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID_CALL, CHANNEL_NAME_CALL, NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setDescription(application.getString(R.string.call_notification_channel_description));
                notifyMgr.createNotificationChannel(channel);
                sp.edit().putBoolean(SP_KEY_CHANNEL_CREATED, true).apply();
            }
        }
    }

    private static RemoteViews buildNotifyView(Context context, SelectedContactBean bean) {
        String name = bean.getName();
        String phone = bean.getPhoneNumber();
        boolean enableTextMsg = bean.isTextMsgEnable();
        boolean enableCall = bean.isCallEnable();
        int requestCode = bean.getNotificationId().hashCode();

        RemoteViews result = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Bitmap avatar = bean.getAvatarBm();
        if (avatar == null) {
            int avatarWidth = context.getResources().getDimensionPixelSize(R.dimen.width_avatar_notification);
            int avatarHeight = context.getResources().getDimensionPixelSize(R.dimen.height_avatar_notification);
            ImageUtils.loadImageToNotification(context, avatarWidth, avatarHeight, bean);
        } else {
            result.setImageViewBitmap(R.id.notification_avatar, avatar);
        }

        result.setTextViewText(R.id.notification_name, name);

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + phone));
        call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent callIntent = PendingIntent.getActivity(
                context, requestCode, call, PendingIntent.FLAG_UPDATE_CURRENT);
        result.setOnClickPendingIntent(R.id.notification_content, callIntent);
        if (enableCall) {
            result.setOnClickPendingIntent(R.id.notification_call, callIntent);
            result.setViewVisibility(R.id.notification_call, View.VISIBLE);
        } else {
            result.setViewVisibility(R.id.notification_call, View.GONE);
        }

        if (enableTextMsg) {
            Intent textMsg = new Intent(Intent.ACTION_SENDTO);
            textMsg.setData(Uri.parse("smsto:" + phone));
            textMsg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent textMsgIntent = PendingIntent.getActivity(
                    context, requestCode, textMsg, PendingIntent.FLAG_UPDATE_CURRENT);
            result.setOnClickPendingIntent(R.id.notification_msg, textMsgIntent);
            result.setViewVisibility(R.id.notification_msg, View.VISIBLE);
        } else {
            result.setViewVisibility(R.id.notification_msg, View.GONE);
        }

        return result;
    }

    public static void removeNotification(Context context, SelectedContactBean bean) {
        context = context.getApplicationContext();
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotifyMgr != null) {
            mNotifyMgr.cancel(bean.getNotificationId().hashCode());
        }
    }

}
