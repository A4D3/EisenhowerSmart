package be.demeurea.eisenhowersmart.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @author Demeure Arnaud
 * @created on 27-02-21
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TASK_KEY = "id";
    public static final String TASK_NAME = "name";
    public static final String TASK_DESCR = "description";
    public static final String TASK_DEADLINE = "deadline";
    public static final String TASK_EMERGENCY = "emergency";
    public static final String TASK_IMPORTANCE = "importance";

    public static final String TASK_TABLE_NAME = "Task";
    public static final String TASK_TABLE_CREATE =
            "CREATE TABLE " + TASK_TABLE_NAME + " (" +
                    TASK_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TASK_NAME + " TEXT NOT NULL, " +
                    TASK_DESCR + " TEXT NOT NULL, " +
                    TASK_DEADLINE + " TEXT, " +
                    TASK_EMERGENCY + " REAL NOT NULL CHECK (" + TASK_EMERGENCY + " >= -10 AND " + TASK_EMERGENCY + " <= 10), " +
                    TASK_IMPORTANCE + " REAL NOT NULL CHECK (" + TASK_IMPORTANCE + " >= -10 AND " + TASK_IMPORTANCE + " <= 10));";


    /**
     * Constructor
     * @param context Context
     * @param name String
     * @param factory SQLiteDatabase.CursorFactory
     * @param version int
     */
    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * If db change.
     * @param db SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        assert db != null : "DatabaseHandler.onCreate: db is null";

        db.execSQL(TASK_TABLE_CREATE);
    }

    /**
     * If version change.
     * @param db SQLiteDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
