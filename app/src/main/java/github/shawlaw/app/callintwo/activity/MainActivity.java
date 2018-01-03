package github.shawlaw.app.callintwo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import github.shawlaw.app.callintwo.R;
import github.shawlaw.app.callintwo.adapter.SelectedAdapter;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.contracts.IMainContract;
import github.shawlaw.app.callintwo.presenter.MainPresenter;

/**
 * 首页，展示已选择的联系人
 * @author Shawlaw
 */
public class MainActivity extends AppCompatActivity implements IMainContract.IView,
        View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private IMainContract.IPresenter mPresenter;

    private ImageView mBtnAddContact;
    private ListView mSelectedList;

    private TextView mListEmptyHolder;

    private SelectedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this, this);
        setContentView(R.layout.activity_main);
        initWidgets();
        mPresenter.loadData();
    }

    private void initWidgets() {
        mBtnAddContact = findViewById(R.id.btn_add_contact);
        mListEmptyHolder = findViewById(R.id.selected_list_empty_holder);
        mSelectedList = findViewById(R.id.selected_list);
        mAdapter = new SelectedAdapter(LayoutInflater.from(this), mPresenter);
        mSelectedList.setAdapter(mAdapter);
        mSelectedList.setEmptyView(mListEmptyHolder);
        mListEmptyHolder.setText(R.string.empty_view_initial);

        mBtnAddContact.setOnClickListener(this);
        mSelectedList.setOnItemClickListener(this);
        mSelectedList.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onActivityStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mPresenter.processActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!mPresenter.processRequestPermissionResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onActivityDestroy();
    }

    @Override
    public void refreshList() {
        mListEmptyHolder.setText(R.string.empty_view_loaded);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshListAsync() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshList();
            }
        });
    }

    @Override
    public void showNoCallPermissionTips() {
        Toast.makeText(this, R.string.tips_no_call_phone_permission, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnAddContact) {
            mPresenter.openPickContactActivity();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedContactBean bean = (SelectedContactBean) parent.getAdapter().getItem(position);
        mPresenter.openContactDetailActivity(bean);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedContactBean bean = ((SelectedContactBean) parent.getAdapter().getItem(position));
        mPresenter.deleteTheContact(bean);
        return true;
    }
}
