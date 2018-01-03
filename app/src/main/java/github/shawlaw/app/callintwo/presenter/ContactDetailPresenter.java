package github.shawlaw.app.callintwo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.concurrent.Future;

import github.shawlaw.app.callintwo.activity.ContactDetailActivity;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.contracts.IContactDetailContract;
import github.shawlaw.app.callintwo.repository.Contacts;
import github.shawlaw.app.callintwo.repository.ContactsLoader;
import github.shawlaw.app.callintwo.utils.PermissionUtils;

/**
 * 联系人详情Presenter层
 * @author Shawlaw
 * @date 2017/12/8
 */

public class ContactDetailPresenter implements IContactDetailContract.IPresenter,
        PermissionUtils.IPermissionCallback {

    private SelectedContactBean mContact;
    private String mNotificationId;

    private Activity mActivity;
    private IContactDetailContract.IView mView;
    private Contacts mContacts;

    private Future mLoadTask;

    public ContactDetailPresenter(Activity activity, IContactDetailContract.IView view) {
        mActivity = activity;
        mView = view;
        mContacts = Contacts.getInstance();
    }

    @Override
    public void startFromIntent(Intent intent) {
        mContact = intent.getParcelableExtra(ContactDetailActivity.CONTACT_BEAN_KEY);
        if (mContact == null) {
            mNotificationId = intent.getStringExtra(ContactDetailActivity.NOTIFICATION_ID_KEY);
            if (mNotificationId == null) {
                throw new RuntimeException("Invalid start! Neither a contactBean nor a notificationId is available!");
            } else {
                mContact = mContacts.getById(mNotificationId);
            }
        }
        if (mContact == null) {
            mView.showLoading();
            loadContactFromProvider();
        } else {
            mView.showContent(mContact);
        }
    }

    private void loadContactFromProvider() {
        if (PermissionUtils.checkPermission(mActivity, Manifest.permission.READ_CONTACTS)) {
            actualStartLoadContactFromProvider();
        } else {
            PermissionUtils.requestPermission(mActivity, Manifest.permission.READ_CONTACTS);
        }
    }

    private void actualStartLoadContactFromProvider() {
        mLoadTask = ContactsLoader.getInstance().loadContact(mActivity, mNotificationId, this);
    }

    @Override
    public boolean processRequestPermissionResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return PermissionUtils.onPermissionRequestResultBack(
                Manifest.permission.READ_CONTACTS, requestCode, permissions, grantResults, this);
    }

    @Override
    public void saveCurrentContact(String name, String phoneNum) {
        SelectedContactBean saveOne = new SelectedContactBean(
                mContact.getNotificationId(), mContact.getAvatarPath(), name, phoneNum,
                mContact.isTextMsgEnable(), mContact.isCallEnable());
        mContacts.add(saveOne);
        mActivity.finish();
    }

    @Override
    public boolean processBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        mActivity = null;
        mView = null;
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
    }

    @Override
    public void onLoadFinish(boolean isSuccess, SelectedContactBean loadedBean) {
        if (isSuccess) {
            mContact = loadedBean;
            mNotificationId = mContact.getNotificationId();
            mView.showContent(mContact);
        } else {
            mView.showLoadFailed();
        }
    }

    @Override
    public void onGranted(String permission) {
        actualStartLoadContactFromProvider();
    }

    @Override
    public void onDenied(String permission) {
        mView.showNoPermissionFailed();
    }
}
