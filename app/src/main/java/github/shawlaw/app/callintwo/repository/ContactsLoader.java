package github.shawlaw.app.callintwo.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import java.util.concurrent.Future;

import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.utils.WorkerUtils;

/**
 * 从ContactProvider异步加载联系人信息的工具类
 * @author Shawlaw
 * @date 2017/12/3
 */

public class ContactsLoader {

    private WorkerUtils mWorker;
    private Handler mMainHandler;

    private ContactsLoader() {
        initWorker();
        initHandler();
    }

    private void initWorker() {
        mWorker = WorkerUtils.getInstance();
    }

    private void initHandler() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public static ContactsLoader getInstance() {
        return InnerInstance.sInstance;
    }

    public Future loadContact(final Context context, final String contactUri, final ILoadCallback callback) {
        return mWorker.submit(new Runnable() {
            @Override
            public void run() {
                String name = "";
                String avatarUri = "";
                String phoneNumber = "";
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(Uri.parse(contactUri),
                        new String[]{ContactsContract.Contacts._ID,
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts.PHOTO_URI},
                        null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        avatarUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                        context.grantUriPermission(context.getPackageName(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                                new String[]{contactId}, null);
                        if (phoneCursor != null) {
                            if (phoneCursor.moveToFirst()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            phoneCursor.close();
                        }
                    }
                    cursor.close();
                }
                final boolean isSuccess = !(name.isEmpty() && avatarUri.isEmpty() && phoneNumber.isEmpty());
                final SelectedContactBean bean = new SelectedContactBean(contactUri, avatarUri, name, phoneNumber, false, false);
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoadFinish(isSuccess, bean);
                    }
                });
            }
        });
    }

    public interface ILoadCallback {
        void onLoadFinish(boolean isSuccess, SelectedContactBean loadedBean);
    }

    private static class InnerInstance {
        private static ContactsLoader sInstance = new ContactsLoader();
    }
}
