package github.shawlaw.app.callintwo.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import github.shawlaw.app.callintwo.repository.Contacts;

/**
 * 用于在开机启动后重新恢复通知栏里的快捷拨号的JobIntentService
 * @author Shawlaw
 */

public class LoadNotificationService extends JobIntentService implements Contacts.ILoadCallback {
    public static final String ACTION_NOTIFY_ALL_CONTACTS = "notifyAllContacts";
    public static final int JOB_ID_NOTIFY_ALL_CONTACTS = 1000;

    private Contacts mContacts;

    public LoadNotificationService() {
        mContacts = Contacts.getInstance();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final String action = intent.getAction();
        if (ACTION_NOTIFY_ALL_CONTACTS.equals(action)) {
            notifyAllContacts();
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, LoadNotificationService.class);
        intent.setAction(ACTION_NOTIFY_ALL_CONTACTS);
        enqueueWork(context, LoadNotificationService.class, JOB_ID_NOTIFY_ALL_CONTACTS, intent);
    }

    private void notifyAllContacts() {
        mContacts.startLoadFromDB(this, this, false);
    }

    @Override
    public void onLoadFinish() {
        mContacts.doNotifyTraversal(this);
        mContacts.closeDB();
    }
}
