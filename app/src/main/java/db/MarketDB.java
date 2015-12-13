package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import target.Item;
import target.Target;

/**
 * Created by ruslanthakohov on 25/11/15.
 */
public class MarketDB {
    private static final String TAG = "MarketDB";

    private final MarketHelper helper;

    public MarketDB(Context context) {
        helper = MarketHelper.getInstance(context);
    }

    /**
     * Adds a new Target to the database
     * @param target     Target object
     * @return           false if there was an error
     */
    public boolean addTarget(Target target) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MarketContract.TargetColumns.TARGET_NAME, target.name);

        long id = db.insert(MarketContract.Targets.TABLE, null, values);
        if (id == -1) {
            Log.e(TAG, "error adding Target to the database");
            return false;
        }
        target.setId(id);

        return true;
    }

    /**
     * Deletes the given target by ID value
     * @param target     Target object
     */
    public void deleteTarget(Target target) {
        SQLiteDatabase db = helper.getWritableDatabase();

        deleteItemsForTarget(target, db);

        db.delete(MarketContract.Targets.TABLE, MarketContract.TargetColumns.TARGET_ID + " = ?",
                new String[]{String.valueOf(target.getId())});
    }

    public void deleteAllTargets() {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(MarketContract.Targets.TABLE, null, null);
        db.delete(MarketContract.Items.TABLE, null, null);
    }

    /**
     * Fetches all Target objects from the database
     * @return     ArrayList of targets
     */
    public List<Target> getAllTargets() {
        String selectQuery = "SELECT  * FROM " + MarketContract.Targets.TABLE;
        List<Target> targets = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = helper.getReadableDatabase().rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    Target target = new Target(cursor.getString(1));
                    target.setId(cursor.getLong(0));
                    targets.add(target);
                }
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "failed to close cursor: " + e.toString());
                }
            }
        }

        return targets;
    }

    public void addItemsForTarget(Target existingTarget, List<Item> items) {
        addItemsForTarget(existingTarget.getId(), items);
    }

    public void addItemsForTarget(long existingTargetId, List<Item> items) {
        SQLiteDatabase db = helper.getWritableDatabase();

        for (Item item: items) {
            addItemForTarget(existingTargetId, item, db);
        }
        //TODO: error handling
    }

    /**
     * Adds an Item and associates it with an existing Target
     * @param existingTargetId     Target ID object (already in the database)
     * @param item               Item object
     * @param db                 SQLiteDatabase object
     * @return                   false if there was an error
     */

    private boolean addItemForTarget(long existingTargetId, Item item, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(MarketContract.ItemColumns.ITEM_NAME, item.getName());
        values.put(MarketContract.ItemColumns.ITEM_URL, item.getUrl());
        values.put(MarketContract.ItemColumns.ITEM_PRICE, item.getPrice());
        values.put(MarketContract.ItemColumns.ITEM_BANKNOTE, item.getBanknote());
        values.put(MarketContract.ItemColumns.ITEM_ID, item.getId());
        values.put(MarketContract.ItemColumns.ITEM_TARGET_ID, existingTargetId);

        item.setTargetId(existingTargetId);

        long id = db.insert(MarketContract.Items.TABLE, null, values);
        if (id == -1) {
            Log.e(TAG, "error while adding Item to the database");
            return false;
        }

        return true;
    }

    public List<Item> getItemsForTarget(Target target) {
        return getItemsForTarget(target.getId());
    }

    public List<Item> getItemsForTarget(long targetId) {
        ArrayList<Item> items = new ArrayList<>();

        //make a query to find all items associated with the given target
        String selectQuery = "SELECT  * FROM " + MarketContract.Items.TABLE + " ti WHERE" +
                " ti." + MarketContract.ItemColumns.ITEM_TARGET_ID + " = " + String.valueOf(targetId);

        Log.d(TAG, selectQuery);

        Cursor cursor = null;

        try {
            cursor = helper.getReadableDatabase().rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setName(cursor.getString(1));
                    item.setUrl(cursor.getString(2));
                    item.setPrice(cursor.getString(3));
                    item.setId(cursor.getLong(0));
                    item.setBanknote(cursor.getString(4));
                    items.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to close cursor");
                }
            }
        }

        return items;
    }

    /**
     * Delete all items associated with a given target
     * @param target     Target object
     */
    public void deleteItemsForTarget(Target target) {
        SQLiteDatabase db = helper.getWritableDatabase();
        deleteItemsForTarget(target, db);
    }

    private void deleteItemsForTarget(Target target, SQLiteDatabase db) {
        String whereClause = MarketContract.ItemColumns.ITEM_TARGET_ID + " = " + String .valueOf(target.getId());
        db.delete(MarketContract.Items.TABLE, whereClause, null);
    }
}