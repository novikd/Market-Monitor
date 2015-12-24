package ru.ifmo.android_2015.marketmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import db.MarketDB;
import list.ItemsRecyclerAdapter;
import list.SelectedListener;
import request.GetItemsService;
import target.Item;

/**
 * Created by ruslanthakohov on 03/12/15.
 */
public class ItemsActivity extends AppCompatActivity implements SelectedListener<Item> {
    private static final String TAG = ItemsActivity.class.getSimpleName();

    List<Item> items;

    private RecyclerView mItemsRecyclerView;
    private ItemsRecyclerAdapter mItemsAdapter;
    private ProgressBar mProgressBar;
    private FetchItemsTask task = null;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_items);

        items = new ArrayList<>();

        mItemsRecyclerView = (RecyclerView) findViewById(R.id.itemsList);
        mProgressBar = (ProgressBar) findViewById(R.id.items_progress_bar);

        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItemsAdapter = new ItemsRecyclerAdapter(this, items);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsAdapter.setSelectListener(this);

        //get target id
        Intent intent = getIntent();
        final long targetId = intent.getLongExtra(SelectTargetActivity.TARGET_ID_EXTRA, 0);

        if (savedInstance != null) {
            task = (FetchItemsTask) getLastCustomNonConfigurationInstance();
        }

        if (task == null) {
            if (intent.getBooleanExtra(SelectTargetActivity.TARGET_LOADED_EXTRA, false)) {
                task = new FetchItemsTask(this);
                task.execute(targetId);
            }
        } else {
            task.attachActivity(this);
        }

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (task == null) {
                    task = new FetchItemsTask(ItemsActivity.this);
                    task.execute(targetId);
                } else {
                    task.attachActivity(ItemsActivity.this);
                }
            }
        };

        IntentFilter filter = new IntentFilter(GetItemsService.UPDATE_TARGET_ACTION);
        registerReceiver(receiver, filter);

        //TODO: unregister receiver

    }

    private void onDataUpdate() {
        mItemsAdapter.setData(items);
        mItemsAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSelected(Item item) {
        Log.i("OnItemSelected", "Item name: " + item.getName());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_ITEM, item);
        Log.d("Image: ", item.getImageUrl());
        startActivity(intent);
    }

    private static class FetchItemsTask extends AsyncTask<Long, Void, FetchState> {
        ItemsActivity activity;
        List<Item> items;
        FetchState status;

        FetchItemsTask(ItemsActivity activity) {
            attachActivity(activity);
        }

        @Override
        public FetchState doInBackground(Long ... params) {
            if (params.length < 1) {
                Log.e(TAG, "Target ID not specified");
                return FetchState.FAIL;
            }

            long targetId = params[0];
            MarketDB db = new MarketDB(activity);

            items = db.getItemsForTarget(targetId);
            Log.d(TAG, String.valueOf(targetId));
            //TODO: check if data hasn't been loaded yet
            if (items.size() == 0) {
                return FetchState.LOADING;
            } else {
                return FetchState.SUCCESS;
            }
        }

        @Override
        public void onPostExecute(FetchState status) {
            this.status = status;

            Log.d(TAG, status.toString());

            updateUI();
        }

        @Override
        public void onProgressUpdate(Void ... progress) {
            updateUI();
        }

        public void attachActivity(ItemsActivity activity) {
            this.activity = activity;
            publishProgress();
        }

        private void updateUI() {
            if (status == FetchState.SUCCESS) {
                activity.items = items;
                activity.onDataUpdate();
            }
        }

        private static final String TAG = FetchItemsTask.class.getSimpleName();
    }
}

enum FetchState {
    SUCCESS, LOADING, FAIL
}
