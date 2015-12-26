package db;

/**
 * Created by ruslanthakohov on 24/11/15.
 */
public final class MarketContract {

    public interface TargetColumns {
        /**
         * Target ID, key
         *
         * SQLite type: INTEGER PRIMARY KEY
         */
        String TARGET_ID = "_id";

        /**
         * Target name
         *
         * SQLite type: TEXT
         */
        String TARGET_NAME = "name";

        /**
         * Indicates whether data for this target
         * has been loaded (=1) or not (=0)
         *
         * SQLite type: INTEGER
         */
        String TARGET_LOADED = "loaded";
    }

    public static final class Targets implements TargetColumns {
        public static final String TABLE = "targets";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE +
                "(" +
                TARGET_ID + " INTEGER PRIMARY KEY, " +
                TARGET_NAME + " TEXT, " +
                TARGET_LOADED + " INTEGER" +
                ")";


    }

    public interface ItemColumns {
        /**
         * Item id
         *
         * SQLite type: TEXT
         */
        String ITEM_ID = "id";

        /**
         * Item name
         *
         * SQLite type: TEXT
         */
        String ITEM_NAME = "name";

        /**
         * Item url
         *
         * SQLite type: TEXT
         */
        String ITEM_URL = "url";

        /**
         * Item price
         *
         * SQLite type: TEXT
         */
        String ITEM_PRICE = "price";

        /**
         * Item banknote
         *
         * SQLite type: REAL
         */
        String ITEM_BANKNOTE = "banknote";

        /**
         * Target ID associated with the item
         *
         * SQLite type: INTEGER
         */
        String ITEM_TARGET_ID = "target_id";

        /**
         * Target ID associated with the item
         *
         * SQLite type: INTEGER
         */
        String ITEM_IMAGE_URL = "image_url";
    }

    public static final class Items implements ItemColumns {
        public static final String TABLE = "items";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE +
                "(" +
                ITEM_ID + " INTEGER, " +
                ITEM_NAME + " TEXT, " +
                ITEM_URL + " TEXT, " +
                ITEM_PRICE + " REAL, " +
                ITEM_BANKNOTE + " TEXT, " +
                ITEM_TARGET_ID + " INTEGER, " +
                ITEM_IMAGE_URL + " TEXT" +
                ")";
    }

    private MarketContract() {}
}
