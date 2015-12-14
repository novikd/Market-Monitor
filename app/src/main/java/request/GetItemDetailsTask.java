package request;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import target.Item;
import target.ItemDetails;

/**
 * Created by novik on 13.12.15.
 */
public class GetItemDetailsTask extends AsyncTask<Item, Void, ItemDetails> {

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

    private ItemDetails readJSON(InputStream in) {
        JsonReader reader = new JsonReader(new InputStreamReader(in));


        return null;
    }
}
