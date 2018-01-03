package github.shawlaw.app.callintwo.repository.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shawlaw on 2017/12/12.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_FILE_NAME = "SelectedContacts.db";
    private static final int DB_VERSION = 1;

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SelectedContactDBEntry.TABLE_NAME + " (" +
                    SelectedContactDBEntry._ID + " INTEGER PRIMARY KEY," +
                    SelectedContactDBEntry.COLUMN_CONTACT_URI + TYPE_TEXT + COMMA_SEP +
                    SelectedContactDBEntry.COLUMN_AVATAR_URI + TYPE_TEXT + COMMA_SEP +
                    SelectedContactDBEntry.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                    SelectedContactDBEntry.COLUMN_PHONE + TYPE_TEXT + COMMA_SEP +
                    SelectedContactDBEntry.COLUMN_BOOLEAN_BITMAP + TYPE_INTEGER + " )";

    public DBOpenHelper(Context context) {
        super(context.getApplicationContext(), DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
