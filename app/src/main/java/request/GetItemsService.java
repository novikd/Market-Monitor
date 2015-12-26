package request;

/**
 * Created by ruslanthakohov on 29/11/15.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import db.MarketDB;
import ru.ifmo.android_2015.marketmonitor.DetailActivity;
import ru.ifmo.android_2015.marketmonitor.R;
import target.Item;

import static android.support.v7.app.NotificationCompat.*;

/**
 * This service makes a FindingService request to the API,
 * parses the JSON respones and writes it to the database
 */
public class GetItemsService extends IntentService {
    private static final String SERVICE_NAME = GetItemsService.class.getSimpleName();
    private static final String TAG = "GetItemsService";

    public static final String TARGET_ID_EXTRA = "targetId";
    public static final String TARGET_NAME_EXTRA = "targetName";

    private MarketDB db;

    public GetItemsService() {
        super(SERVICE_NAME);
        db = new MarketDB(this);
    }

    /**
     * Gets new requests and handles them
     * @param workIntent    URL for making a request to the API
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "Service running");
        //get the URL for request
        String dataString = workIntent.getDataString();
        long targetId = workIntent.getLongExtra(TARGET_ID_EXTRA, -1);
        String targetName = workIntent.getStringExtra(TARGET_NAME_EXTRA);

        URL requestURL = null;
        HttpURLConnection connection = null;
        InputStream in = null;

        try {
            requestURL = new URL(dataString);
            connection = (HttpURLConnection) requestURL.openConnection();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                + ", " + connection.getResponseMessage());
            }

            Item bestItemOld = db.getBestItemForTarget(targetId);
            in = connection.getInputStream();
            db.deleteItemsForTarget(targetId);
            db.addItemsForTarget(targetId, readJsonResponse(in));
            Log.d(TAG, "Data saved to the db");

            //check up on best offer
            Item bestItem = db.getBestItemForTarget(targetId);
            if (bestItem != null) {
                if (bestItemOld == null || !bestItem.equals(bestItemOld)) {
                    Log.d(TAG, "new best item: " + bestItem.getName());
                    makeNotification(targetName, bestItem);


                }
            }

            db.setTargetLoaded(targetId, true);

            makeBroadcast(targetId);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

    }

    public static final String UPDATED_TARGET_ID = "updatedTargetID";
    public static final String UPDATE_TARGET_ACTION = "UPDATE_ITEMS";
    /**
     * Sends a broadcast that items for a target have been updated
     * @param id    id of the updated target
     */
    private void makeBroadcast(long id) {
        Intent intent = new Intent(UPDATE_TARGET_ACTION);
        intent.putExtra(UPDATED_TARGET_ID, id);
        sendBroadcast(intent);

        sendBroadcast(new Intent("UPDATE_TARGETS"));
    }

    /**
     * Make a notification about new minimum price
     */
    private void makeNotification(String targetName, Item item) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle("New offer on " + targetName)
                                .setContentText("Click to see details");

        Intent resultIntent = new Intent(getApplicationContext(), DetailActivity.class);
        resultIntent.putExtra(DetailActivity.EXTRA_ITEM, item);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private List<Item> readJsonResponse(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            return readItemsByKeyworksResponse(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Item> readItemsByKeyworksResponse(JsonReader reader) throws IOException {
        ArrayList<Item> result = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "findItemsByKeywordsResponse":
                    result = readMessages(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();
        reader.close();

        return result;
    }

    private ArrayList<Item> readMessages(JsonReader reader) throws IOException {
        ArrayList<Item> result = null;

        reader.beginArray();
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "searchResult":
                    result = readResult(reader);
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();
        reader.endArray();

        return result;
    }

    private ArrayList<Item> readResult(JsonReader reader) throws IOException {
        ArrayList<Item> result = null;

        reader.beginArray();
        reader.beginObject();

        while(reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "@count":
                    int count = reader.nextInt();
                    if (count == 0) {
                        reader.endObject();
                        return null;
                    }
                    break;
                case "item":
                    reader.beginArray();
                    result = new ArrayList<>();
                    while (reader.hasNext()) {
                        result.add(readItem(reader));
                    }
                    reader.endArray();
                    break;
            }
        }

        reader.endObject();
        reader.endArray();

        return result;
    }

    private Item readItem(JsonReader reader) throws IOException {
        Item res = new Item();
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "title":
                    res.setName(readString(reader));
                    break;
                case "itemId":
                    res.setId(readLong(reader));
                    break;
                case "sellingStatus":
                    reader.beginArray();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String tmp = reader.nextName();
                        if (tmp.equals("currentPrice")) {
                            readPrice(reader, res);
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    reader.endArray();
                    break;
                case "viewItemURL":
                    res.setUrl(readString(reader));
                    break;
                case "galleryURL":
                    res.setImageUrl(readString(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();

        return res;
    }

    private void readPrice(JsonReader reader, Item res) throws IOException {
        reader.beginArray();
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("@currencyId")) {
                res.setBanknote(reader.nextString());
            } else if (name.equals("__value__")) {
                res.setPrice(Float.parseFloat(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.endArray();
    }

    private String readString(JsonReader reader) throws IOException {
        reader.beginArray();
        String res = reader.nextString();
        reader.endArray();
        return res;
    }

    private long readLong(JsonReader reader) throws IOException {
        return Long.parseLong(readString(reader));
    }
}
