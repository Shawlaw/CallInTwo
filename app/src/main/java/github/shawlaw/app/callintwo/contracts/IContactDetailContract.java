package github.shawlaw.app.callintwo.contracts;

import android.content.Intent;
import android.support.annotation.NonNull;

import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.repository.ContactsLoader;

/**
 * Created by Shawlaw on 2017/12/8.
 */

public interface IContactDetailContract {
    interface IPresenter extends ContactsLoader.ILoadCallback {
        void startFromIntent(Intent intent);
        boolean processRequestPermissionResult(
                int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
        void saveCurrentContact(String name, String phoneNum);
        boolean processBackPressed();
        void onDestroy();
    }

    interface IView {
        void showLoading();
        void showLoadFailed();
        void showContent(SelectedContactBean bean);
        void showNoPermissionFailed();
    }
}
