package request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import target.Category;

/**
 * Created by ruslanthakohov on 07/11/15.
 */
public class GetCategoriesTask extends AsyncTask<Void, Void, DownloadState> {
    private GetCategoriesTaskClient client;

    private List<Category> categories = null;

    private DownloadState state = DownloadState.DOWNLOADING;

    public GetCategoriesTask(GetCategoriesTaskClient client) {
        attachClient(client);
    }

    public void attachClient(GetCategoriesTaskClient client) {
        this.client = client;
        publishProgress();

    }

    //TODO: fetch the list of categories
    @Override
    public DownloadState doInBackground(Void ... params) {
        categories = new ArrayList<>();

        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            connection = (HttpURLConnection) Linker.createCategoriesUrl().openConnection();
            in = connection.getInputStream();

            categories = readJSON(in);
        } catch (Exception e) {
            Log.e(TAG, "Getting categories error occurred: " + e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Closing input stream error occurred: " + e);
                    e.printStackTrace();
                }
            }
        }

        return DownloadState.DONE;
    }

    @Override
    public void onProgressUpdate(Void ... values) {
        updateClient();
    }

    @Override
    public void onPostExecute(DownloadState downloadState) {
        state = downloadState;
        updateClient();
    }

    //called on the main thread
    private void updateClient() {
        if (state == DownloadState.DONE) {
            if (client != null) {
                client.categoriesAreReady(categories);
            } else {
                Log.e(TAG, "GetCategoriesTask Client is not set!");
            }
        } else if (state == DownloadState.FAILED) {
            if (client != null) {
                client.downloadFailed();
            } else {
                Log.e(TAG, "GetCategoriesTask Client is not set!");
            }
        }
    }

    private ArrayList<Category> readJSON(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        ArrayList<Category> result = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "CategoryArray":
                    reader.beginObject();
                    result = parseCategoriesList(reader);
                    reader.endObject();
                    break;
                default:
                    reader.skipValue();
            }
        }

        return result;
    }

    private ArrayList<Category> parseCategoriesList(JsonReader reader) throws IOException {
        ArrayList<Category> result = new ArrayList<>();
        String name = reader.nextName();
        reader.beginArray();

        while (reader.hasNext()) {
            Category category = parseCategory(reader);
            if (category != null) {
                result.add(category);
            }
        }

        reader.endArray();

        return result;
    }

    private Category parseCategory(JsonReader reader) throws IOException {
        String categoryName = "", categoryId = "";
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "CategoryID":
                    categoryId = reader.nextString();
                    break;
                case "CategoryName":
                    categoryName = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.endObject();
        if (categoryId.equals("-1")) {
            return null;
        }
        return new Category(categoryName, categoryId);
    }

    private static final String TAG = "GetCategoriesTask";
}