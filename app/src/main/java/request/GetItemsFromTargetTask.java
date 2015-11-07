package request;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import target.Item;

/**
 * Created by novik on 07.11.15.
 */
public class GetItemsFromTargetTask extends AsyncTask<URL, Void, ArrayList<Item> > {

    private Activity activity;

    public GetItemsFromTargetTask(Activity activity) {
        this.activity = activity;
    }

    public void attachActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<Item> doInBackground(URL... params) {
        ArrayList<Item> result = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) params[0].openConnection();
            InputStream inputStream = connection.getInputStream();

            result = readJsonStream(inputStream);

            connection.disconnect();
            return result;
        } catch (Exception e) {
            if (connection != null)
                connection.disconnect();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Item> itemList) {
        super.onPostExecute(itemList);
    }

    private ArrayList<Item> readJsonStream(InputStream is) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(is));

        try {
            return readMessages(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Item> readMessages(JsonReader reader) throws IOException {
        reader.beginArray();

        while (!reader.nextName().equals("searchResult"))
            reader.skipValue();

        ArrayList<Item> res = readResult(reader);

        reader.endArray();

        return res;
    }

    private ArrayList<Item> readResult(JsonReader reader) throws IOException {
        ArrayList<Item> result = new ArrayList<>();

        reader.beginArray();

        while(reader.hasNext()) {
            result.add(readItem(reader));
        }

        reader.endArray();

        return result;
    }

    private Item readItem(JsonReader reader) throws IOException {
        Item res = new Item();
        reader.beginArray();

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "attribute":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("name")) {
                            res.setName(reader.nextString());
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endArray();
                    break;
                case "itemId":
                    res.setId(reader.nextString());
                    break;
                case "discountPriceInfo":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        if (reader.nextName().equals("originalRetailPrice")) {
                            res.setPrice(reader.nextString());
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endArray();
                    break;
                case "viewItemURL":
                    res.setUrl(reader.nextString());
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endArray();

        return res;
    }
}
