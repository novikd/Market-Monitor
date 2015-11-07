package request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignore) {}

        categories.add(new Category("Smartphones"));
        categories.add(new Category("Clothes"));
        categories.add(new Category("Perfume"));
        categories.add(new Category("Food"));
        categories.add(new Category("Accessories"));
        categories.add(new Category("Footwear"));
        categories.add(new Category("Tickets"));

        return DownloadState.DONE;
    }

    @Override
    public void onProgressUpdate(Void ... values) {
        updateClient();
    }

    @Override
    public void onPostExecute(DownloadState downloadState) {
        state = downloadState;
        if (downloadState == DownloadState.DONE) {
            updateClient();
        } else {
            Log.e(TAG, "Failed to fetch categories");
        }
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

    private static final String TAG = "GetCategoriesTask";
}

enum DownloadState {
    DOWNLOADING, DONE, FAILED
}
