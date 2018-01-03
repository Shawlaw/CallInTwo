package github.shawlaw.app.callintwo.repository.database;

import android.provider.BaseColumns;

/**
 * Created by Shawlaw on 2017/12/12.
 */

public final class SelectedContactDBEntry implements BaseColumns {
    public static final String TABLE_NAME = "SelectedContacts";
    public static final String COLUMN_CONTACT_URI = "rawUri";
    public static final String COLUMN_AVATAR_URI = "avatarUri";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_BOOLEAN_BITMAP = "booleanBitmap";
}
