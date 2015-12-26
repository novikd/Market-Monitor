package request;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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
            e.printStackTrace();
            if (connection != null)
                connection.disconnect();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Item> itemList) {
        //TODO: implement it
        super.onPostExecute(itemList);
    }

    private ArrayList<Item> readJsonStream(InputStream is) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(is));
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
