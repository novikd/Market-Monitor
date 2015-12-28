package ru.ifmo.android_2015.marketmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import db.MarketDB;
import list.ItemsRecyclerAdapter;
import list.SelectedListener;
import request.GetItemsService;
import target.Item;
import target.Target;

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
    private BroadcastReceiver receiver;

    private long targetId;
    private boolean shouldReloadOnResume = false;

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
        targetId = intent.getLongExtra(SelectTargetActivity.TARGET_ID_EXTRA, 0);

        setActionBar(intent.getStringExtra(
                SelectTargetActivity.TARGET_NAME_EXTRA));

        shouldReloadOnResume = (targetId == 0);


        if (savedInstance != null) {
            Log.d(TAG, "Saved task found");
            task = (FetchItemsTask) getLastCustomNonConfigurationInstance();
        }

        if (task == null) {
            Log.d(TAG, String.valueOf(intent.getBooleanExtra(SelectTargetActivity.TARGET_LOADED_EXTRA, false)));
            if (intent.getBooleanExtra(SelectTargetActivity.TARGET_LOADED_EXTRA, false)) {
                task = new FetchItemsTask(this);
                task.execute(targetId);
            }
        } else {
            task.attachActivity(this);
        }

        receiver = new BroadcastReceiver() {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionBar(String title) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SelectTargetActivity.SHOULD_RELOAD_TARGETS_EXTRA, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, String.valueOf(shouldReloadOnResume));
        if (shouldReloadOnResume) {
            task = new FetchItemsTask(this);
            task.execute(targetId);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
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

            return FetchState.SUCCESS;
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
