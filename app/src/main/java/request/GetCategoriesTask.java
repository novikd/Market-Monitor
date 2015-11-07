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
public class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
    private GetCategoriesTaskClient client;

    public GetCategoriesTask(GetCategoriesTaskClient client) {
        attachClient(client);
    }

    public void attachClient(GetCategoriesTaskClient client) {
        this.client = client;
    }

    //TODO: fetch the list of categories
    @Override
    public List<Category> doInBackground(Void ... params) {
        ArrayList<Category> list = new ArrayList<>();

        list.add(new Category("Smartphones"));
        list.add(new Category("Clothes"));
        list.add(new Category("Perfume"));
        list.add(new Category("Food"));
        list.add(new Category("Accessories"));
        list.add(new Category("Footwear"));
        list.add(new Category("Tickets"));

        return list;
    }

    @Override
    public void onPostExecute(List<Category> categories) {
        if (client != null) {
            client.categoriesAreReady(categories);
        } else {
            Log.e(TAG, "GetCategoriesTask Client is not set!");
        }
    }

    private static final String TAG = "GetCategoriesTask";
}
