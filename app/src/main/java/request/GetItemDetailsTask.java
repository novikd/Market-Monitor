package request;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ru.ifmo.android_2015.marketmonitor.DetailActivity;
import ru.ifmo.android_2015.marketmonitor.R;
import target.Item;
import target.ItemDetails;

/**
 * Created by novik on 13.12.15.
 */
public class GetItemDetailsTask extends AsyncTask<Item, Void, ItemDetails> {

    private DetailActivity activity;
    private ItemDetails details;

    public GetItemDetailsTask(DetailActivity activity) {
        this.activity = activity;
        details = null;
    }

    public void attachActivity(DetailActivity activity) {
        this.activity = activity;
        updateView();
    }

    private void updateView() {
        if (activity != null && activity.state == DownloadState.DONE) {
            String temp;
            TextView textView = (TextView) activity.findViewById(R.id.item_country);
            temp = "Country: " + details.getCountry();
            textView.setText(temp);
            textView = (TextView) activity.findViewById(R.id.item_location);
            temp =  "Location: " + details.getLocation();
            textView.setText(temp);
            textView = (TextView) activity.findViewById(R.id.item_detail_cost);
            temp = "Price: " + details.getCost() + " " + details.getBanknote();
            textView.setText(temp);
            textView = (TextView) activity.findViewById(R.id.item_type);
            temp = "Selling format: " + details.getType();
            textView.setText(temp);
            textView = (TextView) activity.findViewById(R.id.item_status);
            temp = "Status: " + details.getStatus();
            textView.setText(temp);
        } else if (activity != null && activity.state == DownloadState.FAILED) {
            TextView textView = (TextView) activity.findViewById(R.id.item_detail_name);
            textView.setText("Getting item's details error occurred.");
        }
    }

    @Override
    protected ItemDetails doInBackground(Item... params) {
        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            URL url = Linker.createShopUrl(String.valueOf(params[0].getId()));
            connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();

            ItemDetails details = readJSON(in);
            return details;
        } catch (Exception e) {
            Log.e("GettingDetails", "Error occurred: " + e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private ItemDetails readJSON(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        ItemDetails result = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "Item":
                    reader.beginObject();
                    result = readItem(reader);
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();
        return result;
    }

    private ItemDetails readItem(JsonReader reader) throws IOException {
        ItemDetails result = null;
        String url = "", type = "", status = "", country = "", location = "";
        String cost = "", banknote = "";

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "ConvertedCurrentPrice":
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String temp = reader.nextName();
                        switch (temp) {
                            case "Value":
                                cost = reader.nextString();
                                break;
                            case "CurrencyID":
                                banknote = reader.nextString();
                                break;
                            default:
                                reader.skipValue();
                        }
                    }
                    reader.endObject();
                    break;
                case "ViewItemURLForNaturalSearch":
                    url = reader.nextString();
                    break;
                case "ListingType":
                    type = reader.nextString();
                    break;
                case "ListingStatus":
                    status = reader.nextString();
                    break;
                case "Country":
                    country = reader.nextString();
                    break;
                case "Location":
                    location = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }

        result = new ItemDetails(url, type, status, country, location);
        result.setCost(cost);
        result.setBanknote(banknote);
        return result;
    }

    @Override
    protected void onPostExecute(ItemDetails itemDetails) {
        details = itemDetails;
        if (details != null) {
            activity.state = DownloadState.DONE;
        } else {
            activity.state = DownloadState.FAILED;
        }
        updateView();

        super.onPostExecute(itemDetails);
    }
}
