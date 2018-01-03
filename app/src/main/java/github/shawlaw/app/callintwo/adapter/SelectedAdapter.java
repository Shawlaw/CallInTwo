package github.shawlaw.app.callintwo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import github.shawlaw.app.callintwo.R;
import github.shawlaw.app.callintwo.repository.bean.SelectedContactBean;
import github.shawlaw.app.callintwo.repository.Contacts;
import github.shawlaw.app.callintwo.utils.ImageUtils;

/**
 * 首页已选联系人列表的适配器
 * @author Shawlaw
 * @date 2017/11/28
 */

public class SelectedAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    private final IOnCheckListener mOnCheckListener;
    private final Contacts mContacts;

    public SelectedAdapter(LayoutInflater inflater, IOnCheckListener onCheckListener) {
        mInflater = inflater;
        mOnCheckListener = onCheckListener;
        mContacts = Contacts.getInstance();
    }

    @Override
    public int getCount() {
        return mContacts.getTotalCount();
    }

    @Override
    public SelectedContactBean getItem(int position) {
        return mContacts.getByIndex(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_selected_list, parent, false);
            holder = new ViewHolder(convertView, mOnCheckListener);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.renderWith(getItem(position));
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (mContacts.hasChangeSinceLastQuery()) {
            super.notifyDataSetChanged();
            mContacts.resetChangeStatus();
            mContacts.doNotifyTraversal(mInflater.getContext());
        }
    }


    private static class ViewHolder {
        private static final int TAG_KEY_BEAN = 0x10241024;

        private final View mContent;

        private final ImageView mAvatar;
        private final TextView mName;
        private final TextView mPhoneNumber;
        private final CheckBox mEnableTextMsg;
        private final CheckBox mEnableCall;

        private final CompoundButton.OnCheckedChangeListener mRawTextMsgCheckListener
                = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mOnCheckListener != null) {
                    SelectedContactBean bean = (SelectedContactBean) mContent.getTag(TAG_KEY_BEAN);
                    mOnCheckListener.onTextMsgCheckChange(
                            mEnableTextMsg.isChecked(), bean
                    );
                }
            }
        };

        private final CompoundButton.OnCheckedChangeListener mRawCallCheckListener
                = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mOnCheckListener != null) {
                    SelectedContactBean bean = (SelectedContactBean) mContent.getTag(TAG_KEY_BEAN);
                    mOnCheckListener.onCallCheckChange(
                            mEnableCall.isChecked(), bean
                    );
                }
            }
        };
        private final IOnCheckListener mOnCheckListener;

        private ViewHolder(View contentView, IOnCheckListener listener) {
            mContent = contentView;
            mAvatar = contentView.findViewById(R.id.item_avatar);
            mName = contentView.findViewById(R.id.item_name);
            mPhoneNumber = contentView.findViewById(R.id.item_phone);
            mEnableTextMsg = contentView.findViewById(R.id.item_msg_switch);
            mEnableCall = contentView.findViewById(R.id.item_phone_switch);
            mContent.setTag(this);
            mOnCheckListener = listener;
        }

        private void renderWith(SelectedContactBean bean) {
            mContent.setTag(TAG_KEY_BEAN, bean);
            ImageUtils.loadImage(mAvatar, bean.getAvatarPath());
            mName.setText(bean.getName());
            mPhoneNumber.setText(bean.getPhoneNumber());
            beforeRenderCheckBoxes();
            mEnableTextMsg.setChecked(bean.isTextMsgEnable());
            mEnableCall.setChecked(bean.isCallEnable());
            afterRenderCheckBoxes();
        }

        private void beforeRenderCheckBoxes() {
            mEnableTextMsg.setOnCheckedChangeListener(null);
            mEnableCall.setOnCheckedChangeListener(null);
        }

        private void afterRenderCheckBoxes() {
            mEnableTextMsg.setOnCheckedChangeListener(mRawTextMsgCheckListener);
            mEnableCall.setOnCheckedChangeListener(mRawCallCheckListener);
        }
    }

    public interface IOnCheckListener {
        void onTextMsgCheckChange(boolean isTextMsgChecked, SelectedContactBean bean);
        void onCallCheckChange(boolean isCallChecked, SelectedContactBean bean);
    }
}
