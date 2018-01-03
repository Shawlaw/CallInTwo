package github.shawlaw.app.callintwo.contracts;

import android.content.Intent;
import android.support.annotation.NonNull;

import github.shawlaw.app.callintwo.adapter.SelectedAdapter;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;

/**
 * 主界面的MVP定义
 * @author Shawlaw
 * @date 2017/12/8
 */

public interface IMainContract {
    interface IPresenter extends SelectedAdapter.IOnCheckListener {
        void loadData();
        void onActivityStart();
        void onActivityDestroy();
        void openPickContactActivity();
        boolean processActivityResult(int requestCode, int resultCode, Intent data);
        void openContactDetailActivity(SelectedContactBean bean);
        void deleteTheContact(SelectedContactBean bean);
        boolean processRequestPermissionResult(
                int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    interface IView {
        void refreshList();
        void refreshListAsync();
        void showNoCallPermissionTips();
    }
}
