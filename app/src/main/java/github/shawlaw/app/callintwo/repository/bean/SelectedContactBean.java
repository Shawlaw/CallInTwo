package github.shawlaw.app.callintwo.repository.bean;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import github.shawlaw.app.callintwo.repository.database.SelectedContactDBEntry;
import github.shawlaw.app.callintwo.utils.BitUtils;

/**
 * 具体的联系人信息
 * @author Shawlaw
 * @date 2017/11/28
 */

public class SelectedContactBean implements Parcelable {

    private static final int BIT_MASK_TEXT_MSG_ENABLE = BitUtils.getDeterminedBitMask(1);
    private static final int BIT_MASK_CALL_ENABLE = BitUtils.getDeterminedBitMask(2);

    private String mNotificationId;
    private String mAvatarPath;
    private String mName;
    private String mPhoneNumber;
    private boolean mTextMsgEnable;
    private boolean mCallEnable;

    private Bitmap mAvatarBm;

    public SelectedContactBean(
            String mNotificationId, String mAvatarPath, String mName, String mPhoneNumber,
            boolean mTextMsgEnable, boolean mCallEnable) {
        this.mNotificationId = mNotificationId;
        this.mAvatarPath = mAvatarPath;
        this.mName = mName;
        this.mPhoneNumber = mPhoneNumber;
        this.mTextMsgEnable = mTextMsgEnable;
        this.mCallEnable = mCallEnable;
    }

    public SelectedContactBean(
            String mNotificationId, String mAvatarPath, String mName, String mPhoneNumber,
            int bitmap) {
        this.mNotificationId = mNotificationId;
        this.mAvatarPath = mAvatarPath;
        this.mName = mName;
        this.mPhoneNumber = mPhoneNumber;
        this.mTextMsgEnable = BitUtils.getDeterminedBit(bitmap, BIT_MASK_TEXT_MSG_ENABLE);
        this.mCallEnable = BitUtils.getDeterminedBit(bitmap, BIT_MASK_CALL_ENABLE);
    }

    protected SelectedContactBean(Parcel in) {
        mNotificationId = in.readString();
        mAvatarPath = in.readString();
        mName = in.readString();
        mPhoneNumber = in.readString();
        mTextMsgEnable = in.readByte() != 0;
        mCallEnable = in.readByte() != 0;
    }

    public String getNotificationId() {
        return mNotificationId;
    }

    public String getAvatarPath() {
        return mAvatarPath;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public boolean isTextMsgEnable() {
        return mTextMsgEnable;
    }

    public void setTextMsgEnable(boolean textMsgEnable) {
        mTextMsgEnable = textMsgEnable;
    }

    public boolean isCallEnable() {
        return mCallEnable;
    }

    public void setCallEnable(boolean callEnable) {
        mCallEnable = callEnable;
    }

    public Bitmap getAvatarBm() {
        return mAvatarBm;
    }

    public void setAvatarBm(Bitmap avatarBm) {
        mAvatarBm = avatarBm;
    }

    public void copyFrom(SelectedContactBean src) {
        this.mNotificationId = src.mNotificationId;
        this.mAvatarPath = src.mAvatarPath;
        this.mName = src.mName;
        this.mPhoneNumber = src.mPhoneNumber;
        this.mTextMsgEnable = src.mTextMsgEnable;
        this.mCallEnable = src.mCallEnable;
    }

    public ContentValues getCV() {
        ContentValues cv = new ContentValues();
        cv.put(SelectedContactDBEntry.COLUMN_CONTACT_URI, mNotificationId);
        cv.put(SelectedContactDBEntry.COLUMN_AVATAR_URI, mAvatarPath);
        cv.put(SelectedContactDBEntry.COLUMN_NAME, mName);
        cv.put(SelectedContactDBEntry.COLUMN_PHONE, mPhoneNumber);
        cv.put(SelectedContactDBEntry.COLUMN_BOOLEAN_BITMAP, booleansToBitmap());
        return cv;
    }

    private int booleansToBitmap() {
        int bitmap = BitUtils.setDeterminedBit(0, BIT_MASK_TEXT_MSG_ENABLE, mTextMsgEnable);
        bitmap = BitUtils.setDeterminedBit(bitmap, BIT_MASK_CALL_ENABLE, mCallEnable);
        return bitmap;
    }

    @Override
    public String toString() {
        return "SelectedContactBean{" +
                "mNotificationId='" + mNotificationId + '\'' +
                ", mAvatarPath='" + mAvatarPath + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mTextMsgEnable=" + mTextMsgEnable +
                ", mCallEnable=" + mCallEnable +
                ", mAvatarBm=" + mAvatarBm +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNotificationId);
        dest.writeString(mAvatarPath);
        dest.writeString(mName);
        dest.writeString(mPhoneNumber);
        dest.writeByte((byte) (mTextMsgEnable ? 1 : 0));
        dest.writeByte((byte) (mCallEnable ? 1 : 0));
    }

    public static final Creator<SelectedContactBean> CREATOR = new Creator<SelectedContactBean>() {
        @Override
        public SelectedContactBean createFromParcel(Parcel in) {
            return new SelectedContactBean(in);
        }

        @Override
        public SelectedContactBean[] newArray(int size) {
            return new SelectedContactBean[size];
        }
    };

}
