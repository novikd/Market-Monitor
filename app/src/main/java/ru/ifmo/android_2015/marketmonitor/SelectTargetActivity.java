package ru.ifmo.android_2015.marketmonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;

import db.MarketDB;
import list.RecyclerDividerDecorator;
import list.SelectedListener;
import list.TargetsRecyclerAdapter;
import target.Target;

public class SelectTargetActivity extends AppCompatActivity
        implements SelectedListener<Target> {

    private RecyclerView recyclerView;
    private FetchTargetsTask fetchTargetsTask;
    private TargetsRecyclerAdapter adapter;

    public void targetsAreReady(List<Target> targets) {
        adapter.targetsAreReady(targets);

        /*for (Target target : targets) {
            Intent serviceIntent = new Intent(this, GetItemsService.class);
            try {
                serviceIntent.setData(Uri.parse(Linker.createFindUrl(target.getName()).toString()));
            } catch (Exception e) {
                Log.d(TAG, "URL Exception: " + e.toString());
            }
            serviceIntent.putExtra("TARGET_ID", target.getId());
            startService(serviceIntent);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_target);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        recyclerView = (RecyclerView) findViewById(R.id.targets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TargetsRecyclerAdapter(this);
        adapter.setSelectListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerDividerDecorator(Color.DKGRAY));

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
    public Object onRetainCustomNonConfigurationInstance() {
        return fetchTargetsTask;
    }

    public static final String TARGET_ID_EXTRA = "targetIdExtra";

    @Override
    public void onSelected(Target target) {
        Log.i(TAG, "Target selected: " + target.getName());
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra(TARGET_ID_EXTRA, target.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void addNewTarget(MenuItem menuItem) {
        Log.i(TAG, "Add new button was clicked");
        //TODO: start the AddTargetActivity
        Intent addTargetActivity = new Intent(this, AddTargetActivity.class);
        startActivity(addTargetActivity);
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
            MarketDB helper = new MarketDB(activity.get().getApplicationContext());
            targets =  helper.getAllTargets();

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
