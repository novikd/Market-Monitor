package ru.ifmo.android_2015.marketmonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import db.MarketDB;
import list.CategoriesRecyclerAdapter;
import list.RecyclerDividerDecorator;
import request.GetCategoriesTask;
import request.GetCategoriesTaskClient;
import target.Category;
import target.Target;

public class AddTargetActivity extends AppCompatActivity
                                implements GetCategoriesTaskClient {

    private RecyclerView categoriesList;
    private TextView textTarget;
    private ProgressBar progressBar;

    private List<Category> categories; //model

    private GetCategoriesTask task = null;

    private CategoriesRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);

        //initialize handlers for UI elements
        categoriesList = (RecyclerView) findViewById(R.id.categoriesList);
        textTarget = (TextView) findViewById(R.id.textTarget);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //setup the RecyclerView
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.addItemDecoration(new RecyclerDividerDecorator(Color.DKGRAY));
        adapter = new CategoriesRecyclerAdapter(this, categories);
        categoriesList.setAdapter(adapter);

        if (savedInstanceState != null) {
            task = (GetCategoriesTask) getLastCustomNonConfigurationInstance();
        }

        if (task == null) {
            fetchCategories();
        } else {
            task.attachClient(this);
        }

        //TODO: handle config changes for AddTargetTask
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return task;
    }

    /**
     * Executes AsyncTask to fetch the list of Category objects
     */
    private void fetchCategories() {
        task = new GetCategoriesTask(this);
        Log.d(TAG, "Began fetching categories");
        task.execute();
    }

    /**
     * Implementation of GetCategoriesTaskClient interface
     * @param categories    ArrayList of Category objects
     */
    public void categoriesAreReady(List<Category> categories) {
        Log.d(TAG, "Categories fetched, size = " + categories.size());

        this.categories = categories;

        adapter.updateDataSet(categories);
        adapter.notifyDataSetChanged();

        //fetching completed, hide progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Implementation of GetCategoriesTaskClient interface
     */
    public void downloadFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.categories_load_error, Toast.LENGTH_SHORT).show();
    }

    public void onAddClick(View view) {
        if (textTarget.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.specify_target_error, Toast.LENGTH_SHORT).show();
        } else {

            new AddTargetTask(this).execute(new Target(textTarget.getText().toString()));
        }
    }

    /**
     * Called by AddTargetTask when the new target is added to the database
     * @param target the added target with its id set
     */
    public void onTargetAdded(Target target) {
        //TODO: add constants for result code and success flag
        Intent intent = new Intent();
        intent.putExtra("SUCCESS", true);
        setResult(0, intent);
        finish();
    }

    static class AddTargetTask extends AsyncTask<Target, Void, Target> {
        AddTargetActivity activity;

        Target newTarget = null;

        public AddTargetTask(AddTargetActivity activity) {
            this.activity = activity;
        }

        @Override
        public Target doInBackground(Target ... params) {
            MarketDB helper = new MarketDB(activity);
            helper.addTarget(params[0]);

            Log.d(TAG, "New target saved");

            newTarget = params[0];

            return newTarget;
        }

        @Override
        public void onPostExecute(Target target) {
            updateClient();
        }

        @Override
        public void onProgressUpdate(Void ... params) {
            updateClient();
        }

        private void updateClient() {
            if (activity != null) {
                if (newTarget != null) {
                    activity.onTargetAdded(newTarget);
                }
            } else {
                Log.e(TAG, "No activity attached to AddTargetTask");
            }
        }

        public void attachActivity(AddTargetActivity activity) {
            this.activity = activity;
            publishProgress();
        }
    }

    private static final String TAG = "AddTargetActivity";
}
