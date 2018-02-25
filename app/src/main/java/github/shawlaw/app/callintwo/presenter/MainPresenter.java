package github.shawlaw.app.callintwo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import github.shawlaw.app.callintwo.activity.ContactDetailActivity;
import github.shawlaw.app.callintwo.contracts.IMainContract;
import github.shawlaw.app.callintwo.repository.Contacts;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.utils.NotificationUtils;
import github.shawlaw.app.callintwo.utils.PermissionUtils;

/**
 * 主界面Presenter
 * @author Shawlaw
 * @date 2017/12/8
 */

public class MainPresenter implements IMainContract.IPresenter, PermissionUtils.IPermissionCallback,
        Contacts.ILoadCallback {
    private final int REQUEST_CODE_GET_CONTACT = 123;

    private Activity mActivity;
    private IMainContract.IView mView;
    private Contacts mContacts;

    public MainPresenter(Activity activity, IMainContract.IView view) {
        mActivity = activity;
        mView = view;
        mContacts = Contacts.getInstance();
    }

    @Override
    public void loadData() {
        mContacts.startLoadFromDB(mActivity, this, true);
    }

    @Override
    public void onActivityStart() {
        if (mContacts.getTotalCount() > 0
                && mContacts.hasChangeSinceLastQuery()) {
            if (PermissionUtils.checkPermission(mActivity, Manifest.permission.CALL_PHONE)) {
                mView.refreshList();
            } else {
                PermissionUtils.requestPermission(mActivity, Manifest.permission.CALL_PHONE);
            }
        }
    }

    @Override
    public void onActivityDestroy() {
        mContacts.closeDB(mActivity);
    }

    @Override
    public void openPickContactActivity() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mActivity.startActivityForResult(pickContact, REQUEST_CODE_GET_CONTACT);
    }

    @Override
    public boolean processActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = true;
        if (requestCode == REQUEST_CODE_GET_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedContact = data.getData();
                if (selectedContact != null) {
                    ContactDetailActivity.start(mActivity, selectedContact.toString());
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void openContactDetailActivity(SelectedContactBean bean) {
        if (bean != null) {
            ContactDetailActivity.start(mActivity, bean);
        }
    }

    @Override
    public void deleteTheContact(SelectedContactBean bean) {
        if (bean != null) {
            mContacts.delete(bean.getNotificationId());
            NotificationUtils.removeNotification(mActivity, bean);
            mView.refreshList();
        }
    }

    @Override
    public boolean processRequestPermissionResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return PermissionUtils.onPermissionRequestResultBack(
                Manifest.permission.CALL_PHONE, requestCode, permissions, grantResults, this);
    }

    @Override
    public void onGranted(String permission) {
        mView.refreshList();
    }

    @Override
    public void onDenied(String permission) {
        mContacts.clearAll();
        mView.refreshList();
        mView.showNoCallPermissionTips();
    }

    @Override
    public void onLoadFinish() {
        mView.refreshListAsync();
    }

    @Override
    public void onTextMsgCheckChange(boolean isTextMsgChecked, SelectedContactBean bean) {
        bean.setTextMsgEnable(isTextMsgChecked);
        NotificationUtils.sendNotification(mActivity, bean);
        mContacts.updateEntryDirectly(bean);
    }

    @Override
    public void onCallCheckChange(boolean isCallChecked, SelectedContactBean bean) {
        bean.setCallEnable(isCallChecked);
        NotificationUtils.sendNotification(mActivity, bean);
        mContacts.updateEntryDirectly(bean);
    }
}
