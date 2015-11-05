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

import list.TargetSelectedListener;
import list.TargetsRecyclerAdapter;
import target.Target;

public class SelectTargetActivity extends AppCompatActivity implements TargetSelectedListener {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_target);
        recyclerView = (RecyclerView) findViewById(R.id.targets_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TargetsRecyclerAdapter adapter = new TargetsRecyclerAdapter(this);
        adapter.setTargetSelectedListener(this);
        recyclerView.setAdapter(adapter);
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

    }

    private static final String TAG = "SELECT_TARGET";
}
