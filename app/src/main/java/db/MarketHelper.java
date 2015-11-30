package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ruslanthakohov on 25/11/15.
 */
public class MarketHelper extends SQLiteOpenHelper {
    private static final String TAG = "MarketHelper";

    private static final String DB_FILE_NAME = "market.db";
    private static final int DB_VERSION = 1;

    private static volatile MarketHelper instance;

    public static MarketHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (MarketHelper.class) {
                if (instance == null) {
                    instance = new MarketHelper(context);
                }
            }
        }
        return instance;
    }

    private final Context context;

    public MarketHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION, null/*DBErrorHandler*/);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
        db.execSQL(MarketContract.Targets.CREATE_TABLE);
        db.execSQL(MarketContract.Items.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }
}
