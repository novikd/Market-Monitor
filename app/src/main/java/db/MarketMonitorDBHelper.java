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

//TODO: Add exceptions

/**
 * Class that wraps operations with the database
 * Defines tables for targets and items and methods for managing them
 */
public class MarketMonitorDBHelper extends SQLiteOpenHelper {

    //Database Version and Name
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MarketAnalyzerDB";

    //Table names
    private static final String TABLE_TARGETS = "targets";
    private static final String TABLE_ITEMS = "items";

    //Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";

    //Tartgets table column names

    //Items table column names
    private static final String KEY_URL = "url";
    private static final String KEY_PRICE = "price";
    private static final String KEY_BANKNOTE = "banknote";
    private static final String KEY__ID = "__id";
    private static final String KEY_TARGET_ID = "target_id";

    //Targets table creation statement
    private static final String CREATE_TARGETS_TABLE = "CREATE TABLE " + TABLE_TARGETS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";

    //Items table creation statement
    private static final String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_URL + " TEXT,"
            + KEY_PRICE + " TEXT," + KEY__ID + " TEXT," + KEY_BANKNOTE + " TEXT," +
            KEY_TARGET_ID + " INTEGER)";

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

        long id = db.insert(TABLE_TARGETS, null, values);
        if (id != -1) {
            target.setId(id);
        } else {
            Log.e(TAG, "Failed to add target to the db");
        }
        db.close();
    }

    //getting all targets
    public List<Target> getAllTargets() {
        List<Target> targetList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TARGETS;

        Cursor cursor = getReadableDatabase().rawQuery(selectQuery, null);

        //iterating through all the targets and adding them to the list
        if (cursor.moveToFirst()) {
            do {
                Target target = new Target(cursor.getString(1));
                target.setId(cursor.getLong(0));
                targetList.add(target);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return targetList;
    }

    //deleting a single target
    public void deleteTarget(Target target) {
        SQLiteDatabase db = this.getWritableDatabase();

        deleteItemsForTarget(target, db);

        //delete the target
        db.delete(TABLE_TARGETS, KEY_ID + " = ?",
                new String[]{String.valueOf(target.getId())});
        db.close();
    }

    /**
     * CRUD operations for items
     */

    public void addItemsForTarget(Target existingTarget, Item ... items) {
        SQLiteDatabase db = getWritableDatabase();

        for (Item item: items) {
            addItemForTarget(existingTarget, item, db);
        }

        db.close();
    }

    private void addItemForTarget(Target existingTarget, Item item, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_BANKNOTE, item.getBanknote());
        values.put(KEY__ID, item.getId());
        values.put(KEY_TARGET_ID, existingTarget.getId());

        item.setTargetId(existingTarget.getId());

        db.insert(TABLE_ITEMS, null, values);
    }

    //get all the items associated with a given target
    public List<Item> getItemsForTarget(Target target) {
        ArrayList<Item> items = new ArrayList<>();

        //make a query to find all items associated with the given target
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " ti WHERE" +
                " ti." + KEY_TARGET_ID + " = " + String.valueOf(target.getId());

        Log.d(TAG, selectQuery);

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

    public void deleteItemsForTarget(Target target, SQLiteDatabase db) {
        //delete all items associated with this target
        String whereClause = KEY_TARGET_ID + " = " + String .valueOf(target.getId());
        db.delete(TABLE_ITEMS, whereClause, null);
    }

    public void deleteItemsForTarget(Target target) {
        SQLiteDatabase db = getWritableDatabase();
        deleteItemsForTarget(target, db);
        db.close();
    }

    private static final String TAG = "MarketMonitorDBHelper";
}
