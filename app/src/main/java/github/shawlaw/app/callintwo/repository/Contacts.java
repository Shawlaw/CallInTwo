package github.shawlaw.app.callintwo.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;

import github.shawlaw.app.callintwo.receivers.BootCompleteReceiver;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.repository.database.DBOpenHelper;
import github.shawlaw.app.callintwo.repository.database.SelectedContactDBEntry;
import github.shawlaw.app.callintwo.utils.NotificationUtils;
import github.shawlaw.app.callintwo.utils.WorkerUtils;

/**
 * 数据仓库
 * @author Shawlaw
 * @date 2017/12/2
 */

public class Contacts {
    private final static String DB_WORKER_QUEUE_NAME = "contactsDatabase";

    private final ArrayList<SelectedContactBean> mData;

    private boolean mHasChanged;
    private SQLiteDatabase mDatabase;
    private DBOpenHelper mDatabaseManager;

    private Contacts() {
        resetChangeStatus();
        mData = new ArrayList<>();
    }

    public static Contacts getInstance() {
        return InnerInstance.sInstance;
    }

    public void startLoadFromDB(Context context, final ILoadCallback callback, boolean isAsync) {
        mDatabaseManager = new DBOpenHelper(context.getApplicationContext());
        if (isAsync) {
            WorkerUtils.getInstance().enqueue(new Runnable() {
                @Override
                public void run() {
                    actualLoadFromDB(callback);
                }
            }, DB_WORKER_QUEUE_NAME);
        } else {
            actualLoadFromDB(callback);
        }
    }

    private void actualLoadFromDB(ILoadCallback callback) {
        mData.clear();
        mDatabase = mDatabaseManager.getWritableDatabase();
        Cursor cursor = mDatabase.query(SelectedContactDBEntry.TABLE_NAME,
                null, null, null, null, null,
                SelectedContactDBEntry._ID);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mHasChanged = true;
                int contactUriIndex = cursor.getColumnIndex(SelectedContactDBEntry.COLUMN_CONTACT_URI);
                int avatarUriIndex = cursor.getColumnIndex(SelectedContactDBEntry.COLUMN_AVATAR_URI);
                int nameIndex = cursor.getColumnIndex(SelectedContactDBEntry.COLUMN_NAME);
                int phoneIndex = cursor.getColumnIndex(SelectedContactDBEntry.COLUMN_PHONE);
                int booleanBitmapIndex = cursor.getColumnIndex(SelectedContactDBEntry.COLUMN_BOOLEAN_BITMAP);
                do {
                    String contactUri = cursor.getString(contactUriIndex);
                    String avatarUri = cursor.getString(avatarUriIndex);
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    int booleanBitmap = cursor.getInt(booleanBitmapIndex);
                    SelectedContactBean bean = new SelectedContactBean(contactUri, avatarUri, name, phone, booleanBitmap);
                    mData.add(bean);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        callback.onLoadFinish();
    }

    public int getTotalCount() {
        return mData.size();
    }

    public SelectedContactBean getByIndex(int index) {
        if (index >= mData.size()) {
            return null;
        }
        return mData.get(index);
    }

    public SelectedContactBean getById(String notificationId) {
        SelectedContactBean result = null;
        for (final SelectedContactBean bean : mData) {
            if (bean.getNotificationId().equals(notificationId)) {
                result = bean;
                break;
            }
        }
        return result;
    }

    public void add(SelectedContactBean bean) {
        boolean foundMatch = false;
        for (final SelectedContactBean selectedContactBean : mData) {
            if (selectedContactBean.getNotificationId().equals(bean.getNotificationId())) {
                selectedContactBean.copyFrom(bean);
                updateEntryInDatabase(bean);
                foundMatch = true;
                mHasChanged = true;
                break;
            }
        }
        if (!foundMatch) {
            mData.add(bean);
            addEntryToDatabase(bean);
            mHasChanged = true;
        }
    }

    public void delete(String notificationId) {
        Iterator<SelectedContactBean> iterator = mData.iterator();
        while (iterator.hasNext()) {
            SelectedContactBean bean = iterator.next();
            if (notificationId.equals(bean.getNotificationId())) {
                iterator.remove();
                deleteEntryInDatabase(notificationId);
                mHasChanged = true;
                break;
            }
        }
    }

    public void clearAll() {
        if (getTotalCount() > 0) {
            mHasChanged = true;
            mData.clear();
            deleteAllEntryInDatabase();
        }
    }

    public void updateEntryDirectly(SelectedContactBean bean) {
        updateEntryInDatabase(bean);
    }

    public boolean hasChangeSinceLastQuery() {
        return mHasChanged;
    }

    public void resetChangeStatus() {
        mHasChanged = false;
    }

    public void doNotifyTraversal(Context context) {
        for (final SelectedContactBean bean : mData) {
            NotificationUtils.sendNotification(context, bean);
        }
    }

    private void addEntryToDatabase(final SelectedContactBean bean) {
        WorkerUtils.getInstance().enqueue(new Runnable() {
            @Override
            public void run() {
                if (mDatabase != null) {
                    mDatabase.insert(SelectedContactDBEntry.TABLE_NAME, null, bean.getCV());
                }
            }
        }, DB_WORKER_QUEUE_NAME);
    }

    private void updateEntryInDatabase(final SelectedContactBean bean) {
        WorkerUtils.getInstance().enqueue(new Runnable() {
            @Override
            public void run() {
                if (mDatabase != null) {
                    ContentValues cv = bean.getCV();
                    mDatabase.update(SelectedContactDBEntry.TABLE_NAME, cv,
                            SelectedContactDBEntry.COLUMN_CONTACT_URI + "= ?",
                            new String[]{bean.getNotificationId()});
                }
            }
        }, DB_WORKER_QUEUE_NAME);
    }

    private void deleteEntryInDatabase(final String notificationId) {
        WorkerUtils.getInstance().enqueue(new Runnable() {
            @Override
            public void run() {
                if (mDatabase != null) {
                    mDatabase.delete(SelectedContactDBEntry.TABLE_NAME,
                            SelectedContactDBEntry.COLUMN_CONTACT_URI + "= ?",
                            new String[]{notificationId});
                }
            }
        }, DB_WORKER_QUEUE_NAME);
    }

    private void deleteAllEntryInDatabase() {
        WorkerUtils.getInstance().enqueue(new Runnable() {
            @Override
            public void run() {
                if (mDatabase != null) {
                    mDatabase.delete(SelectedContactDBEntry.TABLE_NAME, null, null);
                }
            }
        }, DB_WORKER_QUEUE_NAME);
    }

    public void closeDB(Context context) {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        if (mDatabaseManager != null) {
            mDatabaseManager.close();
            mDatabaseManager = null;
        }

        BootCompleteReceiver.enableReceiver(context, !mData.isEmpty());
    }

    public interface ILoadCallback {
        void onLoadFinish();
    }

    private static class InnerInstance {
        private static Contacts sInstance = new Contacts();
    }
}
