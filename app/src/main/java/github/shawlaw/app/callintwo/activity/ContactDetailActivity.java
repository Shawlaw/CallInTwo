package github.shawlaw.app.callintwo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import github.shawlaw.app.callintwo.R;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.contracts.IContactDetailContract;
import github.shawlaw.app.callintwo.presenter.ContactDetailPresenter;
import github.shawlaw.app.callintwo.utils.ImageUtils;

/**
 * 联系人详情页面
 * @author Shawlaw
 */
public class ContactDetailActivity extends AppCompatActivity
        implements IContactDetailContract.IView, View.OnClickListener {

    public static final String NOTIFICATION_ID_KEY = "notification_id";
    public static final String CONTACT_BEAN_KEY = "contact_bean";

    private IContactDetailContract.IPresenter mPresenter;

    private ImageView mAvatar;
    private EditText mName;
    private EditText mPhone;
    private Button mBtnSave;
    private ImageView mBtnBack;

    private View mLoadingMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ContactDetailPresenter(this, this);
        setContentView(R.layout.activity_contact_edit);
        initImmersive();
        initWidgets();
        initFromIntent();
    }

    private void initImmersive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int oldSystemUiVisibility = decorView.getSystemUiVisibility();
            int newSystemUiVisibility = oldSystemUiVisibility | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                newSystemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(newSystemUiVisibility);
        }
    }

    private void initWidgets() {
        mAvatar = findViewById(R.id.avatar_detail);
        mName = findViewById(R.id.name_detail);
        mPhone = findViewById(R.id.phone_number_detail);
        mBtnSave = findViewById(R.id.btn_save_detail);
        mBtnSave.setOnClickListener(this);
        mBtnBack = findViewById(R.id.btn_back_detail);
        mBtnBack.setOnClickListener(this);

        mLoadingMask = findViewById(R.id.loading_mask_detail);
    }

    private void initFromIntent() {
        Intent income = getIntent();
        mPresenter.startFromIntent(income);
    }

    @Override
    public void showLoading() {
        mLoadingMask.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadFailed() {

    }

    @Override
    public void showContent(SelectedContactBean bean) {
        mAvatar.setTag(bean.getAvatarPath());
        ImageUtils.loadImage(mAvatar, bean.getAvatarPath());
        mName.setText(bean.getName());
        mPhone.setText(bean.getPhoneNumber());
        mLoadingMask.setVisibility(View.GONE);
    }

    @Override
    public void showNoPermissionFailed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (!mPresenter.processRequestPermissionResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSave) {
            doSaveAction();
        } else if (v == mBtnBack) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.processBackPressed()) {
            super.onBackPressed();
        }
    }

    private void doSaveAction() {
        String name = mName.getText().toString();
        String phone = mPhone.getText().toString();
        mPresenter.saveCurrentContact(name, phone);
    }

    public static void start(Context context, String notificationId) {
        Intent starter = new Intent(context, ContactDetailActivity.class);
        starter.putExtra(NOTIFICATION_ID_KEY, notificationId);
        context.startActivity(starter);
    }

    public static void start(Context context, SelectedContactBean contactBean) {
        Intent starter = new Intent(context, ContactDetailActivity.class);
        starter.putExtra(CONTACT_BEAN_KEY, contactBean);
        context.startActivity(starter);
    }
}
