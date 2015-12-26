package ru.ifmo.android_2015.marketmonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import request.DownloadState;
import request.GetItemDetailsTask;
import target.Category;
import target.Item;

/**
 * Created by novik on 13.12.15.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM = "item";

    private Item item;
    private GetItemDetailsTask detailsTask;

    public DownloadState state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_details);

        item = getIntent().getParcelableExtra(EXTRA_ITEM);
        //TODO: Загружать нормальные картинки, а не эту маленькую.
//        ImageLoader.getInstance().displayImage(item.getImageUrl(), (ImageView) findViewById(R.id.item_detail_image));

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
}
