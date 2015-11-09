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

import target.Item;
import target.Target;

/**
 * Created by ruslanthakohov on 05/11/15.
 */
public class MarketMonitorDBHelper extends SQLiteOpenHelper {

    //Database Version and Name
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MarketAnalyzerDB";

    //Table names
    private static final String TABLE_TARGETS = "targets";
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_TARGETS_ITEMS = "targets_items"; //responsible for
                                                                       //the relationship
                                                                       //between targets and items

    //Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";

    //Tartgets table column names

    //Items table column names
    private static final String KEY_URL = "url";
    private static final String KEY_PRICE = "price";
    private static final String KEY_BANKNOTE = "banknote";
    private static final String KEY__ID = "__id";

    //Targets_items table column names
    private static final String KEY_TARGET_ID = "target_id";
    private static final String KEY_ITEM_ID = "item_id";

    //Targets table creation statement
    private static final String CREATE_TARGETS_TABLE = "CREATE TABLE " + TABLE_TARGETS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";

    //Items table creation statement
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_URL + " TEXT,"
            + KEY_PRICE + " TEXT," + KEY__ID + " TEXT," + KEY_BANKNOTE + " TEXT)";

    private static final String CREATE_TARGETS_ITEMS_TABLE = "CREATE TABLE " + TABLE_TARGETS_ITEMS
            + "(" + KEY__ID + " INTEGER PRIMARY KEY," + KEY_TARGET_ID + " INTEGER," +
            KEY_ITEM_ID + " INTEGER)";

    public MarketMonitorDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DBHelper initialized");
    }

    //Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating tables
        db.execSQL(CREATE_TARGETS_TABLE);
        db.execSQL(CREATE_ITEMS_TABLE);
        db.execSQL(CREATE_TARGETS_ITEMS_TABLE);

        Log.d(TAG, "Tables created");
    }

    //Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGETS);

        //recreate tables
        onCreate(db);

        Log.d(TAG, "DB upgraded");
    }

    /**
     * CRUD operations for targets
     */

    //adding a new target
    public void addTarget(Target target) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, target.getName());

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

    //TODO: delete all items associated with the target
    //deleting a single target
    public void deleteTarget(Target target) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TARGETS, KEY_NAME + " = ?",
                new String[]{String.valueOf(target.getName())});
        db.close();
    }

    /**
     * CRUD operations for items
     */

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_BANKNOTE, item.getBanknote());
        values.put(KEY_ID, item.getId());

        db.insert(TABLE_TARGETS, null, values);

        db.close();
    }

    //get all the items associated with a given target
    public List<Item> getItemsForTarget(Target target) {
        ArrayList<Item> items = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " ti, " +
                TABLE_TARGETS + " tt, " + TABLE_TARGETS_ITEMS + " tti WHERE " +
                "ti." + KEY__ID + " = tti." + KEY_ITEM_ID + "AND tt." + KEY__ID +
                " = tti." + KEY_TARGET_ID;

        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);;

        //iterating through all the targets and adding them to the list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setName(cursor.getString(1));
                item.setUrl(cursor.getString(2));
                item.setPrice(cursor.getString(3));
                item.setId(cursor.getString(4));
                item.setBanknote(cursor.getString(5));
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return items;
    }

    /**
     * Helper functions
     */

    private static final String TAG = "MarketMonitorDBHelper";
}
