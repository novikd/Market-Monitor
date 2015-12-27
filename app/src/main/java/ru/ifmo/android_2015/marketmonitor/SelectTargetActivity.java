package ru.ifmo.android_2015.marketmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;

import alarm.UpdateAllTargetsService;
import db.MarketDB;
import db.MarketHelper;
import list.TargetClickHandler;
import list.RecyclerDividerDecorator;
import list.TargetsRecyclerAdapter;
import request.GetItemsService;
import target.Target;

public class SelectTargetActivity extends AppCompatActivity
        implements TargetClickHandler {

    private RecyclerView recyclerView;
    private FetchTargetsTask fetchTargetsTask;
    private TargetsRecyclerAdapter adapter;

    private BroadcastReceiver receiver = null;

    public void targetsAreReady(List<Target> targets) {
        Log.d(TAG, String.valueOf(targets.size()));
        adapter.targetsAreReady(targets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_target);

        PreferenceManager.setDefaultValues(this, R.xml.preference_screen, false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        recyclerView = (RecyclerView) findViewById(R.id.targets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TargetsRecyclerAdapter(this);
        adapter.setSelectListener(this);
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new RecyclerDividerDecorator(Color.DKGRAY));

        if (savedInstanceState != null) {
            fetchTargetsTask = (FetchTargetsTask) getLastCustomNonConfigurationInstance();
        }

        if (fetchTargetsTask == null) {
            fetchTargetsTask = new FetchTargetsTask(this);
            fetchTargetsTask.execute();
        } else {
            fetchTargetsTask.attachActivity(this);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        //register receiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchTargetsTask = new FetchTargetsTask(SelectTargetActivity.this);
                fetchTargetsTask.execute();
                Log.d(TAG, "Broadcast processed");
            }
        };

        IntentFilter filter = new IntentFilter("UPDATE_TARGETS");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return fetchTargetsTask;
    }

    public static final String TARGET_ID_EXTRA = "targetIdExtra";
    public static final String TARGET_LOADED_EXTRA = "targetLoadedExtra";
    public static final int RELOAD_TARGETS_REQUEST = 100;

    @Override
    public void onSelected(Target target) {
        Log.i(TAG, "Target selected: " + target.getName() + " " + String.valueOf(target.isLoaded()));
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra(TARGET_ID_EXTRA, target.getId());
        intent.putExtra(TARGET_LOADED_EXTRA, target.isLoaded());
        startActivityForResult(intent, RELOAD_TARGETS_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add:
                Log.i(TAG, "Add new button was clicked");
                //TODO: start the AddTargetActivity
                Intent addTargetActivity = new Intent(this, AddTargetActivity.class);
                startActivityForResult(addTargetActivity, 1);
                break;
            case R.id.action_settings:
                Log.d(TAG, "Settings clicked");
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static final String SHOULD_RELOAD_TARGETS_EXTRA = "shouldReloadTargets";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        if (resultCode == RESULT_OK) {
            boolean flag = data.getBooleanExtra(SHOULD_RELOAD_TARGETS_EXTRA, false);
            if (flag) {
                Log.d(TAG, "updating targets");
                fetchTargetsTask = new FetchTargetsTask(this);
                fetchTargetsTask.execute();
            }
        }
    }

    public void onDelete(View v) {
        Log.d("SelectTargerActivity", "onDelete pressed");
        Target target = (Target) v.getTag(R.id.tag_target);
        int position = (int) v.getTag(R.id.tag_position);
        MarketDB helper = new MarketDB(getApplicationContext());
        helper.deleteTarget(target);
        adapter.onDeleteClick(target);
    }

    @Override
    public void onLongClick(Target target) {
        //TODO: Start new activity for asking user
        //How to get access to database?
        MarketDB helper = new MarketDB(getApplicationContext());
        helper.deleteTarget(target);
    }

    private static class FetchTargetsTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<SelectTargetActivity> activity;

        private boolean targetsFetched = false;

        private List<Target> targets;

        FetchTargetsTask(SelectTargetActivity activity) {
            attachActivity(activity);
        }

        @Override
        public Boolean doInBackground(Void ... params) {
            Log.d(TAG, "FetchTargetTask running");
            MarketDB helper = new MarketDB(activity.get().getApplicationContext());
            targets =  helper.getAllTargets();
            Log.d(TAG, "Targets fetched");
            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            targetsFetched = result;
            updateUI();
        }

        @Override
        public void onProgressUpdate(Void ... values) {
            updateUI();
        }

        public void attachActivity(SelectTargetActivity activity) {
            this.activity = new WeakReference<>(activity);
            publishProgress();
        }

        private void updateUI() {
            if (targetsFetched) {
                activity.get().targetsAreReady(targets);
            }
        }
    }

    private static final String TAG = "SELECT_TARGET_ACTIVITY";
}
