package ru.ifmo.android_2015.marketmonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import db.FetchTargetsTask;
import db.FetchTargetsTaskClient;
import db.MarketMonitorDBHelper;
import list.TargetSelectedListener;
import list.TargetsRecyclerAdapter;
import target.Target;

public class SelectTargetActivity extends AppCompatActivity
        implements TargetSelectedListener, FetchTargetsTaskClient {

    private RecyclerView recyclerView;
    private FetchTargetsTask fetchTargetsTask;
    private TargetsRecyclerAdapter adapter;

    public void targetsAreReady(List<Target> targets) {
        adapter.targetsAreReady(targets);
        Log.d("SelectTargetActivity", String.valueOf(targets.size()));
        for (Target target: targets) {
            Log.d("SelectTargetActivity", target.getName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_target);

        recyclerView = (RecyclerView) findViewById(R.id.targets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            fetchTargetsTask = (FetchTargetsTask) getLastCustomNonConfigurationInstance();
        }

        if (fetchTargetsTask == null) {
            fetchTargetsTask = new FetchTargetsTask(this, this);
            fetchTargetsTask.execute();
        } else {
            fetchTargetsTask.attachClient(this);
        }

        adapter = new TargetsRecyclerAdapter(this);
        adapter.setTargetSelectedListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return fetchTargetsTask;
    }

    @Override
    public void onTargetSelected(Target target) {
        Log.i(TAG, "Target selected: " + target.getName());
        //TODO: Add part with starting new activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void addNewTarget(MenuItem menuItem) {
        //TODO: start the AddTargetActivity
    }

    private static final String TAG = "SELECT_TARGET";
}
