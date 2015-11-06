package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import target.Target;

/**
 * Created by ruslanthakohov on 05/11/15.
 */
public class MarketMonitorDBHelper extends SQLiteOpenHelper {

    //Database Version and Name
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MarketAnalyzerDB";

    //Targets table name
    private static final String KEY_ID = "_id";
    private static final String TABLE_TARGETS = "targets";

    //Tartgets table columns name
    private static final String KEY_ITEM_NAME = "item_name";

    public MarketMonitorDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelper initialized");
    }

    //Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating Targets table
        String CREATE_TARGETS_TABLE = "CREATE TABLE " + TABLE_TARGETS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ITEM_NAME + " TEXT)";
        db.execSQL(CREATE_TARGETS_TABLE);
    }

    //Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGETS);

        //recreate tables
        onCreate(db);
    }

    //adding new target
    public void addTarget(Target target) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, target.getName());

        db.insert(TABLE_TARGETS, null, values);

        db.close();
    }

    //getting all targets
    public List<Target> getAllTargets() {
        List<Target> targetList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TARGETS;

        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);

        //iterating through all the targets and adding them to the list
        if (cursor.moveToFirst()) {
            do {
                Target target = new Target(cursor.getString(1));
                targetList.add(target);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return targetList;
    }

    //deleting a single target
    public void deleteTarget(Target target) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TARGETS, KEY_ITEM_NAME + " = ?",
                new String[] { String.valueOf(target.getName()) });
        db.close();
    }

    private static final String TAG = "MarketMonitorDBHelper";
}
