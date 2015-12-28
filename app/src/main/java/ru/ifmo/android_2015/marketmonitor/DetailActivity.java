package ru.ifmo.android_2015.marketmonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import db.MarketDB;
import request.DownloadState;
import request.GetItemDetailsTask;
import target.Category;
import target.Item;

/**
 * Created by novik on 13.12.15.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "item";

    private Button favouritesButton;
    private boolean isFavourite = false;

    private Item item;
    private GetItemDetailsTask detailsTask;

    public DownloadState state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_details);

        favouritesButton = (Button) findViewById(R.id.favourites_button);
        favouritesButton.setEnabled(false);

        item = getIntent().getParcelableExtra(EXTRA_ITEM);
        isInFavourites();
        //TODO: Загружать нормальные картинки, а не эту маленькую.
//        ImageLoader.getInstance().displayImage(item.getImageUrl(), (ImageView) findViewById(R.id.item_detail_image));

        setActionBar();

        if (savedInstanceState != null) {
            detailsTask = (GetItemDetailsTask) getLastNonConfigurationInstance();
        }

        if (detailsTask == null) {
            state = DownloadState.DOWNLOADING;
            detailsTask = new GetItemDetailsTask(this);
            detailsTask.execute(item);
        } else {
            detailsTask.attachActivity(this);
        }
    }

    private void setActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle("Offer Details");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("STATE", state);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        state = (DownloadState) savedInstanceState.getSerializable("STATE");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return detailsTask;
    }

    public void onButtonClick(View v) {
        if (item != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
            startActivity(intent);
        }
    }

    public void onFavouritesClick(View v) {
        new AddToFavouritesTask().execute(!isFavourite);
    }

    private void isInFavourites() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void ... params) {
                MarketDB db = new MarketDB(getApplicationContext());
                return db.isFavouriteItem(item);
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    isFavourite = true;
                    favouritesButton.setText("Remove from favourites");
                }
                favouritesButton.setEnabled(true);
            }
        }.execute();
    }

    private class AddToFavouritesTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean ... params) {
            boolean add = params[0];

            MarketDB db = new MarketDB(getApplicationContext());
            if (add) {
                List<Item> items = new ArrayList<>();
                items.add(item);
                db.addItemsForTarget(0, items);
            } else {
                db.deleteItem(item);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            isFavourite = !isFavourite;
            if (isFavourite) {
                favouritesButton.setText(R.string.remove_from_favourites_string);
            } else {
                favouritesButton.setText(getResources().getString(
                        R.string.add_to_favourites_string));
            }
        }
    }
}
